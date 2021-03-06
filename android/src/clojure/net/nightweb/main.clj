(ns net.nightweb.main
  (:require [neko.notify :as notify]
            [neko.resource :as r]
            [neko.ui.mapping :as mapping]
            [net.clandroid.service :as service]
            [nightweb.router :as router]))

(mapping/defelement :scroll-view
                    :classname android.widget.ScrollView
                    :inherits :view)
(mapping/defelement :frame-layout
                    :classname android.widget.FrameLayout
                    :inherits :view)
(mapping/defelement :relative-layout
                    :classname android.widget.RelativeLayout
                    :inherits :view)
(mapping/defelement :image-view
                    :classname android.widget.ImageView
                    :inherits :view)
(mapping/defelement :view-pager
                    :classname android.support.v4.view.ViewPager
                    :inherits :view)

(def ^:const service-name "net.nightweb.MainService")
(def ^:const shutdown-receiver-name "ACTION_CLOSE_APP")

(service/defservice
  net.nightweb.MainService
  :def service
  :on-create
  (fn [this]
    (service/start-foreground
      this 1 (notify/notification
               :icon (r/get-resource :drawable :ic_launcher)
               :content-title (r/get-string :shut_down_nightweb)
               :content-text (r/get-string :nightweb_is_running)
               :action [:broadcast shutdown-receiver-name]))
    (service/start-receiver
      this
      shutdown-receiver-name
      (fn [context intent]
        (try
          (.stopSelf service)
          (catch Exception e nil))))
    (router/start-router (.getAbsolutePath (.getFilesDir this))))
  :on-destroy
  (fn [this]
    (service/stop-receiver this shutdown-receiver-name)
    (router/stop-router)))
