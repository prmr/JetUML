# JetUML Tips

<div id=body></div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>


<style>
/* Snippet taken from https://www.w3schools.com/howto/howto_js_collapsible.asp */
  .collapsible 
  {
    background-color: #777;
    color: white;
    cursor: pointer;
    padding: 18px;
    width: 100%;
    border: none;
    text-align: left;
    outline: none;
    font-size: 15px;
  }

  .active, .collapsible:hover 
  {
    background-color: #555;
  }

  .content 
  {
    padding: 0 18px;
    display: none;
    overflow: hidden;
    background-color: #f1f1f1;
  }
</style>

<script>

  jQuery.get('src/ca/mcgill/cs/jetuml/JetUML.properties', data => 
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

      for(var j = 1; j <= numTips; j++)
      {
        var tipFileName = "tip-" + j + ".json";
        var tipPath = "tipdata/tips/" + tipFileName;

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
  	var tipContent = $('<div/>', 
      {
        class: "content",
      }
    );

    var collapsibleTip = $('<button/>', 
      {
        text: data["title"],
        class: 'collapsible',
      }
    );  
    $("#body").append(collapsibleTip);
    $("#body").append(tipContent);

    collapsibleTip.on("click", function() //function snippet taken from 
      { //https://www.w3schools.com/howto/howto_js_collapsible.asp
        this.classList.toggle("active");
        var content = this.nextElementSibling;
        if (content.style.display === "block") 
        {
          content.style.display = "none";
        } 
        else 
        {
          content.style.display = "block";
        }
      }
    );

    // looping over the tip contents and adding the tip elements to tipContent
    var content = data["content"];
    for (var k = 0; k<content.length; k++)
    {
      var tipElement = content[k];
      for(var type in tipElement)
      {
        if(type == "text")
        {
          var tipText = $('<p/>', 
	        {
              text: tipElement["text"],
            }
          );
          tipContent.append(tipText);
        }
        else if (type == "image")
        {
          var tipImage = $('<img/>', 
  	        {
              src: "tipdata/tip_images/" + tipElement["image"],
            }
          );
          tipContent.append(tipImage);
        }
      }
    }
    $("#body").append("<br/>");
  }

</script>