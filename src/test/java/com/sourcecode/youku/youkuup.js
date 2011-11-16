for(var i=0;i<1000;i++){
$jq.post("http://v.youku.com/QVideo/~ajax/updown?__rt=1&__ro=", { __ap: '{"videoId": "XMzIyODAwNDI0", "type": "up"}' },
   function(data) {
     console.info("Data Loaded: " + data);
   });

}
alert("done");