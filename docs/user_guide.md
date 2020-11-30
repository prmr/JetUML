# User Guide

<div id=body></div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

<style>
/* Snippet inspired from https://www.w3schools.com/howto/howto_js_collapsible.asp */
  .collapsible 
  {
    background-color: #DCDCDC;
    color: black;
    cursor: pointer;
    padding: 18px;
    width: 100%;
    border: none;
    text-align: left;
    outline: none;
    font-size: 15px;
  }

  .active
  {
    background-color: #C0C0C0
  } 

  .collapsible:hover 
  {
    background-color: #AFDBF5;
  }

  .content 
  {
    padding: 0 18px;
    overflow: hidden;
    background-color: #f1f1f1;
    max-height: 0;
    transition: max-height 0.2s ease-out;
  }

  .br
  {
  	margin-top: 4px;
  }
</style>

<script>

  // fetching the properties to get the number of tips
  jQuery.get('../src/ca/mcgill/cs/jetuml/JetUML.properties', data => 
    {
      var numTips = 0;
      var lines = data.split("\n");
      for(var i = 0; i<lines.length; i++)
      {
      	var line = lines[i];
      	if (line.includes("tips.quantity="))
      	{
      		numTips = line.split("tips.quantity=")[1];
      		break;
      	}
      }

      // adding all tips to the DOM
      for(var j = 1; j <= numTips; j++)
      {
        var tipFileName = "tip-" + j + ".json";
        var tipPath = "../tipdata/tips/" + tipFileName;

        // requesting the tip json for the j'th tip and adding it to 
        // the DOM with parseTip
        $.ajax(
          { 
            url: tipPath, 
            dataType: 'json', 
            data: data, 
            async: false, 
            success: parseTip
          } 
        );
      }
    }
  );

  function parseTip(data)
  {
    // The div containing the contents of a given tip
  	var tipContent = $('<div/>', 
      {
        class: "content",
      }
    );

    // the button that opens up the tip content
    var collapsibleTip = $('<button/>', 
      {
        text: data["title"],
        class: 'collapsible',
      }
    );  
    //adding both to the body div
    $("#body").append(collapsibleTip);
    $("#body").append(tipContent);

    // adding handler for the button
    collapsibleTip.on("click", function() //function snippet taken from 
      { //https://www.w3schools.com/howto/howto_js_collapsible.asp
        this.classList.toggle("active");
        var content = this.nextElementSibling;
        if (content.style.maxHeight) 
        {
          content.style.maxHeight = null;
        } 
        else 
        {
          content.style.maxHeight = content.scrollHeight + "px";
        }
      }
    );

    // looping over the tip contents and adding the tip elements to tipContent (div)
    var content = data["content"];
    for (var k = 0; k<content.length; k++)
    {
      var tipElement = content[k];
      for(var type in tipElement)
      {
        if(type == "text")
        {
          //creating a <p> node with the text
          var tipText = $('<p/>', 
	        {
              text: tipElement["text"],
            }
          );
          tipContent.append(tipText);
        }
        else if (type == "image")
        {
          //creating an <img> node with the image
          var tipImage = $('<img/>', 
  	        {
              src: "../tipdata/tip_images/" + tipElement["image"],
            }
          );
          tipContent.append(tipImage);
        }
      }
    }
    // adding a <br> to separate the buttons 
    $("#body").append($('<br/>',
      {
        class: 'br',
      }
    ));
  }

</script>