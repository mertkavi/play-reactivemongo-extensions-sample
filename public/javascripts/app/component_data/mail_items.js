'use strict';

define(
    [
        'flight/lib/component',
        'bower_components/mustache/mustache',
        'app/templates'
    ],

    function (defineComponent, Mustache, templates) {
        return defineComponent(mailItems);

        function mailItems() {

            this.defaultAttrs({
                folder: 'inbox'
            });

            this.serveMailItems = function (ev, data) {
                var self = this;
                var folder = (data && data.folder) || this.attr.folder;
                $.ajax({
                    url: '/mails/' + folder,
                    method: 'GET',
                    dataType: 'json'
                }).done(function (data) {
                    self.trigger("dataMailItemsServed", {markup: self.renderItems(data)}
                    );
                });
            };

            this.renderItems = function (items) {
                return Mustache.render(templates.mailItem, {mailItems: items});
            };

            this.after("initialize", function () {
                this.on("uiMailItemsRequested", this.serveMailItems);
                this.on("dataMailItemsRefreshRequested", this.serveMailItems);
            });
        }
    }
);