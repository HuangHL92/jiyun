var ws = null;

function WsConnect(server){
    var wsUrl = $('#wsUrl').val();
    if ('WebSocket' in window){
        ws = new ReconnectingWebSocket(wsUrl + "/" + server);
    } else{
        $modal.msg("该浏览器不支持websocket");
        $('#div_notice').html("您浏览器不支持websocket，部分功能将无法实现哦！");
    }

    ws.onmessage = function(evt) {
        var data = evt.data;
        console.log("接收的websocket消息:" + data);
        // if (!typeof(socketCallback)=="undefined")
        // {
            socketCallback(data);
        //}

        $('#div_notice').css("alert alert-success");
        $('#div_notice').html("接收的websocket消息:" + data );
    };

    ws.onclose = function(evt) {
        console.log("websocket连接中断...");
        $('#div_notice').css("alert alert-danger");
        $('#div_notice').html("websocket连接中断，请检查配置！（" + wsUrl+")" );
    };

    ws.onopen = function(evt) {
        console.log("websocket连接成功...");
        $('#div_notice').css("alert alert-success");
        $('#div_notice').html("websocket连接成功！（" + wsUrl+")" );
    };

}