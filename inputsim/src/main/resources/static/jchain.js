reloadReports();

function reloadReports() {

    $("div.report").each(function () {
        var target = this;
        $.getJSON('http://' + $(this).attr("data-host") + '/api/difficulty', function (data) {
            $(target).find("span.difficulty").html(data);
        });
        $.getJSON('http://' + $(this).attr("data-host") + '/api/algorithm', function (data) {
            $(target).find("span.algorithm").text(data);
        });
        $.getJSON('http://' + $(this).attr("data-host") + '/api/chain', function (data) {
            var chain = $("<div></div>");
            for (var i = 0; i < data.length; i++) {
                var obj = data[i];
                var hash = $("<p></p>").text(">> Hash: " + obj.hash);
                var parentHash = $("<p></p>").text(">> ParentHash: " + obj.parentHash);
                var nonce = $("<p></p>").text(">> Nonce: " + obj.nonce);
                var contents = $("<div></div>");
                contents.append($("<p></p>").text(">> Contents:"));
                for (var y = 0; y < obj.content.length; y++) {
                    contents.append($("<p></p>").text(" || " + obj.content[y].index + ":" + obj.content[y].text));
                }
                contents.append($("<p></p>"));
                var block = $("<div class='block'></div>");
                block.append(hash);
                block.append(parentHash);
                block.append(nonce);
                block.append(contents);
                chain.append(block);
            }
            $(target).find("div.blockChain").html(chain);
        });
        $.getJSON('http://' + $(this).attr("data-host") + '/api/orphaned', function (data) {
            var orphaned = $("<div></div>");
            for (var i = 0; i < data.length; i++) {
                var obj = data[i];
                var hash = $("<p></p>").text(">> Hash: " + obj.hash);
                var parentHash = $("<p></p>").text(">> ParentHash: " + obj.parentHash);
                var nonce = $("<p></p>").text(">> Nonce: " + obj.nonce);
                var contents = $("<div></div>");
                contents.append($("<p></p>").text(">> Contents:"));
                for (var y = 0; y < obj.content.length; y++) {
                    contents.append($("<p></p>").text("||" + obj.content[y].index + ":" + obj.content[y].text));
                }
                contents.append($("<p></p>"));
                var block = $("<div class='block'></div>");
                block.append(hash);
                block.append(parentHash);
                block.append(nonce);
                block.append(contents);
                orphaned.append(block);

            }
            $(target).find("div.orphanedBlocks").html(orphaned);
        });
        $.getJSON('http://' + $(this).attr("data-host") + '/api/messages', function (data) {
            var messages = $("<div></div>");
            for (var i = 0; i < data.length; i++) {
                var obj = data[i];
                var message = $("<p></p>").text(obj.index + ":" + obj.text);
                messages.append(message);

            }
            $(target).find("div.newMessages").html(messages);
        });

    });

    setTimeout(reloadReports, 1000);
}