package com.horstmann.violet.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;


/**
 * This class provides a FileService for Java Web Start. 
 * Note that file saving is strange under Web Start. You first save the data, and the dialog is only 
 * displayed when the output stream is closed. Therefore, the file name is not available until after
 * the file has been written. 
 */
public class JNLPFileService extends FileService
{
   public JNLPFileService()
   {
      try
      {
      	openService = (FileOpenService) ServiceManager.lookup("javax.jnlp.FileOpenService"); 
      	saveService = (FileSaveService) ServiceManager.lookup("javax.jnlp.FileSaveService");
      }
      catch (UnavailableServiceException ex)
      {
      	ex.printStackTrace();
      }
   }

   public FileService.Open open(String defaultDirectory, String defaultFile, 
      ExtensionFilter filter) throws IOException
   {
      if (defaultDirectory == null) defaultDirectory = ".";
      final FileContents contents 
         = openService.openFileDialog(defaultDirectory, filter.getExtensions());
      return new 
         FileService.Open()
         {
            public String getName() throws IOException { return contents.getName(); }
            public InputStream getInputStream() throws IOException { return contents.getInputStream(); }
         };
      }

   public FileService.Save save(final String defaultDirectory, 
      final String defaultFile, final ExtensionFilter filter,
      final String removeExtension, final String addExtension) throws IOException
   {
      return new 
         FileService.Save()
         {
            public String getName() throws IOException 
            { 
               if (contents == null)
                  return null;
               else
                  return contents.getName(); 
            }
            
            public OutputStream getOutputStream() throws IOException 
            { 
               return out;
            }
            
            public void showDialog() throws IOException
            {
               contents = saveService.saveFileDialog(defaultDirectory, 
                  filter.getExtensions(), new ByteArrayInputStream(bout.toByteArray()),
                  editExtension(defaultFile, removeExtension, addExtension));
            }
            
            private ByteArrayOutputStream bout = new ByteArrayOutputStream();
            private OutputStream out = new
               FilterOutputStream(bout)
               {
                  public void close()
                  {
                     try
                     {
                     	super.close();
                        showDialog();
                     }
                     catch (IOException ex)
                     {
                     	ex.printStackTrace();  
                     }                    
                  }
               }; 
            private FileContents contents;
         };
   }
   
   public boolean isWebStart() { return true; }   

   private FileOpenService openService;
   private FileSaveService saveService;
}
