'use strict';

define(
    function() {
        var mailItem =
            '{{#mailItems}}\
              <tr id="{{id}}" class="mail-item">\
                <td class="span2 mailContact">{{contact}}</td>\
                <td class="span8">\
                  <span class="mailSubject">\
                    {{subject}}\
                  </span>\
                  <span class="mailMessage">\
                    - <a href="#">{{message}}</a>\
                  </span>\
                </td>\
              </tr>\
            {{/mailItems}}';

        var composeBox =
            '<div class="modal-body compose-body">\
            <div id="compose_contact" class="{{#newMail}}hint{{/newMail}}{{^newMail}}compose-header{{/newMail}}" contentEditable="true">\
                {{to}}\
              </div>\
              <div id="compose_subject" class="{{#newMail}}hint{{/newMail}}{{^newMail}}compose-header{{/newMail}}" contentEditable="true">\
                {{subject}}\
              </div>\
              <div id="compose_message" class="hint" contentEditable="true">\
                {{message}}\
              </div>\
            </div>\
            <div class="modal-footer">\
              <button id="send_composed" class="btn btn-primary">Send</button>\
              <button id="cancel_composed" class="btn">Cancel</button>\
            </div>';

        var moveToSelector =
            '<ul class="nav nav-list">\
            {{#moveToItems}}\
              <li id="{{.}}" class="move-to-item">{{.}}</li>\
            {{/moveToItems}}\
            </ul>';

        return {
            mailItem: mailItem,
            composeBox: composeBox,
            moveToSelector: moveToSelector
        }
    }
);