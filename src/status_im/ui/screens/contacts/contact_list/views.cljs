(ns status-im.ui.screens.contacts.contact-list.views
  (:require-macros [status-im.utils.views :refer [defview letsubs]])
  (:require [re-frame.core :as re-frame]
            [status-im.components.contact.contact :as contact-view]
            [status-im.components.list.views :as list]
            [status-im.components.react :as react]
            [status-im.components.status-bar :as status-bar]
            [status-im.components.toolbar-new.view :refer [toolbar-with-search toolbar]]
            [status-im.components.toolbar-new.actions :as act]
            [status-im.components.drawer.view :as drawer-view]
            [status-im.ui.screens.contacts.styles :as styles]
            [status-im.ui.screens.contacts.views :as contact-options]
            [status-im.i18n :as i18n]))


(defn render-row [group edit?]
  (fn [row _ _]
    [contact-view/contact-view {:contact        row
                                :on-press       #(re-frame/dispatch [:open-chat-with-contact %])
                                :extended?      edit?
                                :extend-options (contact-options/contact-options row group)}]))

(defview contact-list-toolbar-edit [group]
  [toolbar {:nav-action     (act/back #(re-frame/dispatch [:set-in [:contacts/list-ui-props :edit?] false]))
            :actions        [{:image :blank}]
            :title          (if-not group
                              (i18n/label :t/contacts)
                              (or (:name group) (i18n/label :t/contacts-group-new-chat)))}])

(defview contact-list-toolbar [group]
  (letsubs [show-search [:get-in [:toolbar-search :show]]
            search-text [:get-in [:toolbar-search :text]]]
    (toolbar-with-search
      {:show-search?       (= show-search :contact-list)
       :search-text        search-text
       :search-key         :contact-list
       :title              (if-not group
                             (i18n/label :t/contacts)
                             (or (:name group) (i18n/label :t/contacts-group-new-chat)))
       :search-placeholder (i18n/label :t/search-contacts)
       :actions            [(act/opts [{:text (i18n/label :t/edit)
                                        :value #(re-frame/dispatch [:set-in [:contacts/list-ui-props :edit?] true])}])]})))

(defview contacts-list-view [group edit?]
  (letsubs [contacts [:all-added-group-contacts-filtered (:group-id group)]]
    [list/flat-list {:style                     styles/contacts-list
                     :data                      contacts
                     :render-fn                 (render-row group edit?)
                     :enableEmptySections       true
                     :keyboardShouldPersistTaps :always
                     :header                    list/default-header
                     :footer                    list/default-footer}]))

(defview contact-list []
  (letsubs [edit? [:get-in [:contacts/list-ui-props :edit?]]
            group [:get-contact-group]]
    [drawer-view/drawer-view
     [react/view {:flex 1}
      [react/view
       [status-bar/status-bar]
       (if edit?
         [contact-list-toolbar-edit group]
         [contact-list-toolbar group])]
      [contacts-list-view group edit?]]]))

