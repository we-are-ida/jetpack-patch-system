
(function(window, document, $, Granite) {
    "use strict";

    var ui = $(window).adaptTo("foundation-ui");

    function showResults(result) {
        if (typeof result.patches === "undefined") {
            ui.alert("No new patches", result.message, "info");
            return;
        }

        var message = $(document.createElement("div"));

        var intro = $(document.createElement("h3"));
        intro.text(result.patches.length + " patches were triggered:");
        intro.appendTo(message);

        var list = $(document.createElement("ul"));
        list.appendTo(message);

        for (var i = 0; i < result.patches.length; i++) {
            var patch = result.patches[i];

            var item = $(document.createElement("li"));
            item.text(patch);
            item.appendTo(list);
        }

        ui.alert("Patches triggered", message.html(), "success");
    }

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq-admin.jetpack.patchsystem.action.trigger",
        handler: function(name, el, config, collection, selections) {

            // show spinner
            ui.wait();

            var deferred = $.Deferred();
            $.ajax({
                url: "/services/patches/trigger",
                type: "POST",
                contentType: "application/json"
            }).fail(function(result, opt) {
                ui.clearWait();
                console.error(opt);
                ui.alert("Failed", "Could not trigger the Patch System", "error");
            }).done(function(result) {
                ui.clearWait();
                console.log(result);
                showResults(result);
            })
             .always(function () {
                 deferred.resolve();
             });

            deferred.promise();
        }
    });
})(window, document, Granite.$, Granite);