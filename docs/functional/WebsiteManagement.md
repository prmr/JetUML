# Website Management

## Scope

Website Management concerns how the files of the website are organized, and how to make adjustments to the layout, content and styling.

## Design

The [JetUML website](https://www.jetuml.org) is hosted on the [GitHub repository](https://github.com/prmr/JetUML), and it uses Jekyll, a static website generator with built-in support for GitHub. The overall structure of the website is organized in three folders: one for the layout, another for the content, and one for CSS. Configuring the website to use a pre-built Jekyll theme is done by creating a __config.yml_ file in the repo, and specifying the name of the theme, which for JetUML, is the theme [Slate](https://github.com/pages-themes/slate). The following provides a starting point to navigate the website files in the repo, and to make changes to the website pages. For a more detailed explanation of the folder organization, naming conventions and syntax, refer to the [Jekyll Docs](https://jekyllrb.com/docs/). 

### Layout

All html files that make the layout of the website is stored in the folder __layouts_. Each file in this folder represents a different layout that a page of the website can use. Developers can make modifications to the existing layout, or create a new layout by creating a new file, and declaring the layout of a page to be the name of the new file in the [front matter](https://jekyllrb.com/docs/front-matter/).

### Content

In addition to the layout, a page contains the content, the main pieces of information on the page. The content is written in a markdown file, which can also contain html, which Jekyll will automatically convert it all to html. The content of the home page of JetUML is the _README.md_ file, while all other content is organized in the _docs_ folder. Links to different pages of the website can easily be made by referring to the path of a content flie.
	
### Styling

The CSS for html components are organized in a single file, _style.scss_, located in the assets/css folder. It should be noted that the styles specified in this file exist on top of the pre-defined CSS in the [Slate GitHub repository](https://github.com/pages-themes/slate/blob/master/_sass/jekyll-theme-slate.scss). To change or add CSS for a html component, modify the _style.scss_ file.