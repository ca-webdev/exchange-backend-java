const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/exchange-websocket'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/orderbookupdates', (orderbookupdate) => {
        showOrderBookUpdate(orderbookupdate.body);
    });
    stompClient.subscribe('/topic/recenttrades', (recentTrade) => {
        showRecentTrade(recentTrade.body);
    });
    stompClient.subscribe('/topic/ohlc', (ohlc) => {
        showRecentTrade(ohlc.body);
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#orderbookupdates").html("");
    $("#recenttrades").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.publish({
        destination: "/app/orderinsert",
        body: JSON.stringify({'instruction': $("#instruction").val()})
    });
}

function showOrderBookUpdate(message) {
    $("#orderbookupdates").append("<tr><td>" + message + "</td></tr>");
}

function showRecentTrade(message) {
    $("#recenttrades").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());
});