(function(window, document, $, Granite) {
    "use strict";

    var ui = $(window).adaptTo("foundation-ui");
    var COMMAND_URL = Granite.HTTP.externalize("/bin/wcmcommand");
    var deleteText = Granite.I18n.get("Run");
    var cancelText = Granite.I18n.get("Cancel");

    function progressTicker(title, message) {
        var el = new Coral.Dialog();
        el.backdrop = Coral.Dialog.backdrop.STATIC;
        el.header.textContent = title;
        el.header.insertBefore(new Coral.Wait(), el.header.firstChild);
        el.content.innerHTML = message || "";

        document.body.appendChild(el);
        el.show();

        return {
            finished: function(message) {
                el.header.textContent = "Triggered (async)";
                el.content.innerHTML = message;

                var b = new Coral.Button();
                b.label.textContent = "Close";
                b.variant = "primary";

                b.on("click", function(e) {
                    //ui.clearWait();
                    window.location.reload();
                });

                el.footer.appendChild(b);
            },
            updateMessage: function(message) {
                el.content.innerHTML = message;
            },
            clear: function() {
                el.hide();

                requestAnimationFrame(function() {
                    el.remove();
                });
            }
        };
    }

    function runGroovyScripts(paths, projects, scripts, types, runnables) {

        var tickerMessage = $(document.createElement("div"));

        var wt = progressTicker("Processing", "Starting run ...");

        // creates an anonymous function that executes a run request for the specified path
        // and returns a promise that resolves after the call has completed (successfully or not)
        function createRunRequest(path, project, script, type, runnable) {
            return function () {

                wt.updateMessage(tickerMessage.html()
                    + path + "&nbsp;&nbsp; [in progress ...]<br/>");

                var deferred = $.Deferred();
                $.ajax({
                    url: COMMAND_URL,
                    type: "POST",
                    data: {
                        _charset_: "UTF-8",
                        cmd: "triggerSinglePatch",
                        path: path,
                        type: type,
                        runnable: runnable
                    }
                }).fail(function() {
                    //console.error("Failed to run", path);
                    $(document.createElement("div"))
                        .html(project+": "+ script + "&nbsp;&nbsp; <b class='groovy-run--failed'>Trigger failed</b>")
                        .appendTo(tickerMessage);
                }).done(function() {
                    //console.log("Ran successfully", path);
                    $(document.createElement("div"))
                        .html(project+": "+ script + "&nbsp;&nbsp; <b class='groovy-run--success'>Triggered successfully</b>")
                        .appendTo(tickerMessage);
                }).always(function () {
                    deferred.resolve();
                    wt.updateMessage(tickerMessage.html());
                });

                return deferred.promise();
            };
        }

        // show spinner
        //ui.wait();

        // chain run requests to execute them sequentially to avoid concurrent modifications
        var requests = $.Deferred();
        requests.resolve();
        console.log('paths', paths.length);
        for (var i = 0; i < paths.length; i++) {
            var path = paths[i];
            var project = projects[i];
            var script = scripts[i];
            var type = types[i];
            var runnable = runnables[i];
            requests = requests.then(createRunRequest(path, project, script, type, runnable));
        }

        // hide spinner and reload page after all requests have been executed
        requests.always(function() {
            wt.finished(tickerMessage.html());

            setTimeout(function () {
                //ui.clearWait();
                //window.location.reload();
            }, 3000);
        });
    }

    $(window).adaptTo("foundation-registry").register("foundation.collection.action.action", {
        name: "cq-admin.jetpack.patchsystem.action.rungroovy",
        handler: function(name, el, config, collection, selections) {
            var message = $(document.createElement("div"));

            var intro = $(document.createElement("p")).appendTo(message);
            console.log('selections', selections);
            if (selections.length === 1) {
                intro.text(Granite.I18n.get("You are going to run the following Groovy script:"));
            } else {
                intro.text(Granite.I18n.get("You are going to run the following {0} Groovy scripts:", selections.length));
            }

            var list = [];
            var maxCount = Math.min(selections.length, 12);
            for (var i = 0, ln = maxCount; i < ln; i++) {
                var title = $(selections[i]).find(".foundation-collection-item-script").text();
                list.push($("<b>").text(title).html());
            }
            if (selections.length > maxCount) {
                list.push("&#8230;"); // &#8230; is ellipsis
            }

            $(document.createElement("p")).html(list.join("<br>")).appendTo(message);
            console.log(selections);
            ui.prompt(deleteText, message.html(), "notice", [{
                text: cancelText
            }, {
                text: deleteText,
                primary: true,
                handler: function() {
                    var paths = selections.map(function(v) {
                        return $(v).data("path");
                    });

                    var projects = selections.map(function(v) {
                        return $(v).data("project");
                    });

                    var scripts = selections.map(function(v) {
                        return $(v).data("script");
                    });

                    var types = selections.map(function(v) {
                        return $(v).data("type");
                    });

                    var runnables = selections.map(function(v) {
                        return $(v).data("runnable");
                    });

                    runGroovyScripts(paths, projects, scripts, types, runnables);
                }
            }]);
        }
    });
})(window, document, Granite.$, Granite);