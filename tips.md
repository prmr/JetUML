# JetUML Tips

<div id=body></div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>

<script>

  

  $.ajaxSetup(
    {
      async: false
    }
  );

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
        $.getJSON(tipPath, data =>
          {
          	var collapsibleTip = $('<button/>', 
          	{
              text: data["title"],
              id: 'button_j',
              class: 'collapsible',
              click: function () { alert('hi'); }
            });
            $("#boddy").append(collapsibleTip);
          }
        );
      }
    }
  );

</script>