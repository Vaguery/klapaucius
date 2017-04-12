(change-defaults  :fact-filter #(and (not (:slow %1))
                                     (not (:acceptance %1))
                                     (not (:danger %1))
                                     (not (:parallel %1))
                                     )
                  :visible-future true
                  :print-level :print-namespaces
                 )
