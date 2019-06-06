(function (window, document, $, Granite) {
    "use strict";

    $(document).ready(function (e) {
        $('.foundation-collection-item-showOutput__link').on('click', function (e) {
            e.preventDefault();

            var scriptTitle = $(this).data('script');
            console.log($(this));
            var output = $(this).siblings()[0].innerHTML;
            console.log(output);

            progressTicker(scriptTitle, output)
        });

        function progressTicker(title, message) {
            var el = new Coral.Dialog();
            el.backdrop = Coral.Dialog.backdrop.STATIC;
            el.header.textContent = title;
            el.content.innerHTML = message || "";
            el.content.innerHTML = message;

            var b = new Coral.Button();
            b.label.textContent = "Close";
            b.variant = "primary";

            b.on("click", function (e) {
                el.hide();
            });

            el.footer.appendChild(b);

            document.body.appendChild(el);
            el.show();

            return {
                updateMessage: function (message) {
                    el.content.innerHTML = message;
                },
                clear: function () {
                    el.hide();

                    requestAnimationFrame(function () {
                        el.remove();
                    });
                }
            };
        }
    });
})(window, document, Granite.$, Granite);