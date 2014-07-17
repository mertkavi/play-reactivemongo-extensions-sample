'use strict';

define(
    [
        'flight/lib/component',
        'bower_components/mustache/mustache',
        'app/templates'
    ],

    function (defineComponent, Mustache, templates) {
        return defineComponent(moveTo);

        function moveTo() {

            this.serveAvailableFolders = function (ev, data) {
                this.trigger("dataMoveToItemsServed", {
                    markup: this.renderFolderSelector(this.getOtherFolders(data.folder))
                })
            };

            this.renderFolderSelector = function (items) {
                return Mustache.render(templates.moveToSelector, {moveToItems: items});
            };

            this.moveItems = function (ev, data) {
                switch (data.toFolder) {
                    case "later":
                    case "sent":
                    case "inbox":
                    case "trash":
                        $.ajax({
                            url: '/mails/' + data.toFolder + "/" + data.itemIds,
                            method: 'GET',
                            dataType: 'json'
                        }).done(
                            this.trigger('dataMailItemsRefreshRequested', {folder: data.fromFolder})
                        );
                        break;
                }
            };

            this.getOtherFolders = function (folder) {
                return ["inbox", "later", "sent", "trash"].filter(function (e) {
                    return e != folder
                });
            };

            this.after("initialize", function () {
                this.on("uiAvailableFoldersRequested", this.serveAvailableFolders);
                this.on("uiMoveItemsRequested", this.moveItems);
            });
        }
    }
);
