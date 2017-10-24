(ns status-im.ui.screens.network-settings.views
  (:require-macros [status-im.utils.views :as views])
  (:require [re-frame.core :as re-frame]
            [status-im.components.action-button.action-button :as action-button]
            [status-im.components.action-button.styles :as action-button-styles]
            [status-im.components.react :as react]
            [status-im.components.icons.vector-icons :as vi]
            [status-im.components.list.views :as list]
            [status-im.components.common.common :as common]
            [status-im.ui.screens.network-settings.styles :as st]
            [status-im.components.status-bar :as status-bar]
            [status-im.components.toolbar-new.view :as toolbar-new]
            [status-im.i18n :as i18n]))

(defn network-icon [connected? size]
  [react/view (st/network-icon connected? size)
   [vi/icon :icons/network {:color (if connected? :white :gray)}]])

(defn network-badge [& [{:keys [name connected? options]}]]
  [react/view st/network-badge
   [network-icon connected? 56]
   [react/view {:padding-left 16}
    [react/text {:style st/badge-name-text}
     (or name (i18n/label :t/new-network))]
    (when connected?
      [react/text {:style st/badge-connected-text}
       (i18n/label :t/connected)])]])

(defn actions-view []
  [react/view action-button-styles/actions-list
   ;; TODO(rasom): uncomment add-new-network button when it will be functional,
   ;; https://github.com/status-im/status-react/issues/2104
   #_[react/view {:opacity 0.4}]
    [action-button/action-button
     {:label     (i18n/label :t/add-new-network)
      :icon      :icons/add
      :icon-opts {:color :blue}}]
   #_[context-menu                                          ; TODO should be implemented later
      [action-button-view (i18n/label :t/add-new-network) :add_blue]
      [{:text (i18n/label :t/add-json-file) :value #(dispatch [:navigate-to :paste-json-text])}
       {:text (i18n/label :t/paste-json-as-text) :value #(dispatch [:navigate-to :paste-json-text])}
       {:text (i18n/label :t/specify-rpc-url) :value #(dispatch [:navigate-to :add-rpc-url])}]]])

(defn render-network [current-network]
  (fn [{:keys [id name config] :as network}]
    (let [connected? (= id current-network)]
      [react/touchable-highlight
       {:on-press #(re-frame/dispatch [:navigate-to :network-details network])}
       [react/view st/network-item
        [network-icon connected? 40]
        [react/view {:padding-horizontal 16}
         [react/text {:style st/network-item-name-text}
          name]
         (when connected?
           [react/text {:style st/network-item-connected-text}
            (i18n/label :t/connected)])]]])))

(views/defview network-settings []
  (views/letsubs [{:keys [network networks]} [:get-current-account]]
    [react/view {:flex 1}
     [status-bar/status-bar]
     [toolbar-new/toolbar {:title (i18n/label :t/network-settings)}]
     [react/view {:flex 1}
      [list/flat-list {:style     st/networks-list
                       :data      (vals networks)
                       :render-fn (render-network network)
                       :header    [react/view
                                   [actions-view
                                    [common/bottom-shadow]
                                    [common/form-title (i18n/label :t/existing-networks)
                                     {:count-value (count networks)}]
                                    [common/list-header]]]
                       :footer    [react/view
                                   [common/list-footer]
                                   [common/bottom-shadow]]}]]]))