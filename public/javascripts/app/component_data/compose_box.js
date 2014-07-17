'use strict';

define(
    [
        'flight/lib/component',
        'bower_components/mustache/mustache',
        'app/templates'
    ],

    function (defineComponent, Mustache, templates) {
        return defineComponent(composeBox);

        function composeBox() {

            this.defaultAttrs({
                recipientHintId: 'recipient_hint',
                subjectHint: 'Subject',
                messageHint: 'Message',
                toHint: 'To',
                forwardPrefix: 'Fw',
                replyPrefix: 'Re'
            });

            this.serveComposeBox = function (ev, data) {
                this.trigger("dataComposeBoxServed", {
                    markup: this.renderComposeBox(data.type),
                    type: data.type});
            };

            this.getSubject = function(type) {
                var subjectLookup = {
                    newMail: this.attr.subjectHint,
                    forward: this.attr.forwardPrefix + ": " + this.attr.subjectHint,
                    reply: this.attr.replyPrefix + ": " + this.subjectHint
                };
                return subjectLookup[type];
            };

            this.renderComposeBox = function (type) {
                return Mustache.render(templates.composeBox, {
                    newMail: type == 'newMail',
                    reply: type == 'reply',
                    subject: this.getSubject(type),
                    message: this.attr.messageHint,
                    to: this.attr.toHint
                });
            };

            this.send = function (ev, data) {
                $.ajax({
                    url: '/mails',
                    method: 'POST',
                    dataType: 'json',
                    data: JSON.stringify(data),
                    contentType: 'application/json; charset=utf-8'
                }).done(
                    //this.trigger('dataMailItemsRefreshRequested', {folder: "sent"})
                );
            };

            this.after("initialize", function () {
                this.on("uiComposeBoxRequested", this.serveComposeBox);
                this.on("uiSendRequested", this.send);
            });
        }
    }
);
