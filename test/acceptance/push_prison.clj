(ns acceptance.push-prison
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.types.core :as types]
            [push.util.stack-manipulation :as u]
            [clojure.string :as s])
  (:use midje.sweet)
  (:use [push.interpreter.core])
  (:use [push.interpreter.templates.one-with-everything])
  )


(defn overloaded-interpreter
  [prisoner]
  (-> (make-everything-interpreter :config {:step-limit 20000}
                                   :bindings (:bindings prisoner)
                                   :program (:program prisoner))
      reset-interpreter))



(defn check-on-prisoner
  [prisoner]
  (let [interpreter (overloaded-interpreter prisoner)]
    (try
      (do
        (println (str "\n\nrunning:" (pr-str (:program interpreter)) "\nwith inputs: " (pr-str (:bindings interpreter))))
        (loop [s interpreter]
          (if (is-done? s)
            (println "DONE")
            (recur (do 
              (println (str 
                            ; "\n >> generator: " (pr-str (u/get-stack s :generator))
                            "\n items on :exec " (u/get-stack s :exec)
                            "\n items on :integer " (u/get-stack s :integer)
                            "\n items on  :strings " (u/get-stack s :strings)
                            ; "\n >> error: " (pr-str (u/get-stack s :error))
                            "\n\n"
                            (pr-str (u/peek-at-stack s :log))
                            ; "\n" (get-in s [:config :max-collection-size])
                            
                              ))
              (step s))))))
      (catch Exception e (do 
                            (println 
                              (str "caught exception: " 
                                    (.getMessage e)
                                     " running "
                                     (pr-str (:program interpreter)) "\n"
                                     (pr-str (:bindings interpreter))))
                            (throw (Exception. (.getMessage e))))))))

(def prisoners
  [
  ;; caught exception: java.lang.Exception: Push Parsing Error: Cannot interpret '' as a Push item. running
 {
    :program 

  '[#{:input!2 \i :integer-cutflip :chars-butlast :strings-remove :code-container :vector-sampler :chars-pop} :ref-notequal? :set-difference :integers->code :integers-portion :string-dup 3261523 [] :char-swap "< ©9YÔQq«£C2" :input!10 [275] :chars-nth :chars-take :set-tagwithinteger :char-echo :integer->string #{:chars-echo 260845471 [\Ê \@ \b \× \( \½] :floats-tagwithfloat :code-position :booleans-store :ref-dup :generator-store} :code-container :tagspace-min :integers-portion #{104.00390625 [\6 \T \Õ \¶ \? \ª \Ú] :vector-swap (:input!9 #{147.9296875 :string>? :integers-indexof :string-conjchar :generator-shove true :chars-tagwithinteger :strings-shove} :floats-swap :vector-rerunall #{:string≤? :integers-stackdepth false :code-shove ([\² \:] :environment-new :char-letter? \J :strings-byexample) "dÕÂ" :chars-stackdepth #{:ref-exchange :float-flipstack :vector-emptyitem? (:code-later 896692924 \E [0.0078125 0.0859375 0.0234375 0.1015625 0.12109375 0.08984375 0.01171875] :code-position) #{:strings-stackdepth 824398642 true :float-notequal? 923757351 :boolean-flipstack :code-do :floats-first} [0.0390625 0.0390625 0.15234375] :input!4 :code-comprehension}}) [0.05078125 0.109375] :booleans-replace :code-echoall :chars-indexof} 133162244 :log-empty? #{:tagspace-tagwithinteger [] :booleans-butlast :strings-yank :input!8 "¡çÜ|<wâ" :booleans-generalize} [3735] ["¸5+4ÄÖ`Du@¿" "ÂÁ>n" ">[M¾\\åÌlEniRM1?" "`~ßyË¦rä|!Hlßâ" "6ä?" "¶¦Å" "=××¡¢Æ"] "v­t¸jÑÖS^nß®Vã>" [0.1171875 0.08984375 0.1015625 0.02734375 0.03515625 0.046875 0.03125 0.1328125] [true true true false true true false] :integer-totalistic3 :generator-tagwithinteger :string-yank [] :string-cutflip "TIM~AVisWDd_;§" :integers-pop :integer->numerals :chars-pop [3257 1054 3720 3331 300 1659 343] :boolean-rerunall ["0wH}`ºIa" "2ªsmmØËÂB©aäTPÅ" "­¨C½ÜæJ!á%æÛ\\º½" "ÅJ¶i*ÝHÝ" "(«pÇ:­ÂØäâÄ+åÑ×y" "°Cº'eQ>Ö`¿pO®" "äæ4§QC%" "_4oxx" "v¯I"] true :vector-cutstack :vector-flush ([3548 3647] :code-null? :exec-flipstack :vector->set :chars-empty?) :integer-rerunall :booleans-pop :chars-cycler :vector-flipstack :strings-swap :input!7 :strings-cycler #{112.96484375 :exec->string :char-dup :float-flipstack true :input!4 :exec-return :ref-dup} :tagspace-dup :floats-pop \# :boolean-faircoin :integer-againlater "zm0taÖ9!Ô¨äKE" ["­i³\\Î©0N¼¤ÛC"] :integers-first (:integer-lots ([false false true] :vector-new [0.0625 0.0625 0.09765625 0.1484375 0.1484375 0.0234375 0.078125 0.078125] (:tagspace-liftstack (:refs-flipstack :input!9 :chars-first #{[false false true true true false false true] :exec-print [] true :code-store :chars-last false :boolean-later} ("hL66¾­«.ÅÐÛ}Á#Y" :tagspace-yank [\( \& \â \µ \, \= \Q \H] :tagspace-keys :floats-yank)) :boolean-return-pop (\ª :refs->tagspaceint [2303 367 1764 482 4535] 38.265625 :float-liftstack) :tagspace-shove) "dÖbÒÝ!µ¶³bá»DK") :ref-tagwithinteger :tagspace-swap 286.90625) true :floats-cutflip :integers-print [1669 2247 1596 542 4896 3769 4481] :char-yank :input!2 :char<? :ref-known? [] :refs-tagwithinteger [0.125 0.03515625] :vector-refilterall :tagspace-tidywithints :tagspace-new 169.18359375 [\Ö \} \h \¼ \£ \¦ \N] :integer-flipstack :float-return :floats-notequal? :code-rotate (:code-equal? :chars-portion :code-cons :ref-print :set-echoall) :refs-empty? :input!9 :integers-build :integers-dup [true true false true true] :vector-new [3887 3416 1412 3112 4475 1576 3333] :tagspace-pop :tagspace-empty? #{:generator-rerunall :chars-build [true false true false false false false false] :strings-yank \µ :chars-empty? #{["gw" "hÒÓÖÚÊK1ÈÇæ" "" "" "3Á`pØ" "CäÜª2+£¯H" "^S=L=zÔÏCÛ\"4Üg\\" "Ü°" "^bsmcàV^ÚÓZä¤á"] :string-stackdepth #{39.984375 "#a-¶Vk" [1333 3932] :float-stackdepth ["<<ËHÁaËuÇçÄ8" "Â&i¦Læá§|-ÆKq;Ì" ">" "¼\\pu^DuÏ\\Þ'ÛQ" "Wlæ~Óád!0¦®çh_" "DÓÚ²ÀÙ.¯& `XHU" "1¡\\©REU" "t6ÔÃâHi^9iÊ!¶z,5}" "À]%SàFMnkÒ<Ð4 U"] (#{[true false true false true true true] :integers-cutstack :set-notequal? [true] ([\s \¹ \W \Ä] :string-min false :chars-cutflip :vector-contains?) :input!7 :string-rerunall [0.0390625 0.02734375 0.05078125 0.01953125]} [] "Z(" :boolean-tagwithfloat 716501012) [0.140625 0.09765625] :ref-lookup} 371.59765625 :string-max :integers-occurrencesof :floats-storestack :set->code} :char-flipstack} :refs-tagwithinteger 349.67578125 :integer-rerunall :float-abs :float-shove :integers-echo :input!10 "'?9À»ª3lÄ¢¦æ" :code-nth [3209 4806 4686 2952 3926 4886 1687] :tagspace-lookupintegers :float-echoall :integers-cutstack :integers-replace :code-size :floats-pop :float-storestack #{:refs-rest :integers-rest :integers-equal? :string-flipstack ["6×\"Æ´FrÌ«0¹"] :strings-butlast :vector-length :refs-indexof} \¶ :strings-liftstack :integers-return \« :float-yankdup :refs-equal? :float-dup :input!6 :tagspace-empty? :exec-do*range :float-echo false :floats-do*each :ref-tagwithfloat :float-liftstack :char-return :strings-generalizeall :integer-min :refs-reverse "MÖã²ÉÍ" [] #{226.7890625 249.953125 371.6953125 :chars-byexample true [0.15234375 0.05078125 0.046875 0.09375 0.05078125 0.1328125 0.15234375 0.09765625] :float-stackdepth (:integer-rerunall :char>? :set-store :set-store :floats-flush)} [ ] "»qCL" :refs-build :booleans-liftstack :integer-uniform [0.01953125] (\É "Û" [\ \B \9 \Å \6 \¤ \« \r \¸] [] :set->tagspacefloat) 304.30078125 :integer-smallest 248.71484375 #{["«»c"] ["uBµT¨$¾:¨j1=%" "c{hNq¦·'Ys" "W§¡Ý]§.Á+&r:2" "½(nÍ"] "ÖÄËÆmsç" [\£ \. \ß \i] [2677 4153 3800 4698 4011 3786 4131 2204] #{[\U \³ \? \É \_ \ç \[] :float-empty? 550854280 :input!1 :set-intersection 580051625 [false true true true true true] :float->integer} :refs-save} ["H" "Ö¬" "à]¦" "*Yt.¡6)" "²" "£2±¨¶;8 °yÙÈ" "Í¤Ç4¨(" "ÜLÈ"] :float-notequal? (:exec-dup true :integers-tagwithinteger :vector-equal? :string-rerunall) 288269733 :code-do* :float-equal? 257967664 [0.09375 0.06640625 0.05859375 0.01953125 0.0859375 0.04296875 0.15234375 0.05078125] :floats-set :integer-cutflip [0.09375 0.01171875 0.140625] [354 4893 4471] :generator-tagwithinteger :string-min ["K@" "½g(°Åd¿WÀÃa²Q»v" "¸L£" "Ý`Ï£4" "áØS¥RáÜá" "Ì¹©IÛ&8Á·æ4I" "h{P|ÖppBDÙÂÈA<M" ",µXQ,dÇF¹°Rä"] ["½Q~Lq" "4rÅÑ¯5/)åRÚ" "æ¿m`¸" "»]2RJAÃ0à" "f[-Å<Z¸cØv6Ú" "fz:eZ5>" "+L°ØË[ lÁ¨!â¦<;Ã[E" "," "5<OnÞÜvÊÀ,-Ås¬"] :ref-storestack :refs-return-pop :strings-emptyitem? :refs-remove ["É¹9«<g©" " b áji£¯" "Eâá½àxn°Ð\"#A" "Pád?¿7M®±L¦<L¾"] 318.25390625 :char>? "çÄÝh­67ÊM¾yB+~~bÄ" ["ÈOÎ\\âÊÌ" " (m8ÛBäLä\"Ç3" "»" "Q" "Ù`¸;E4bT<·?~rÆ" "º" "¼ljMÎªLs¨<" "©ÂR·1#e"] [4005 603 3895 1231 33 3200] :integers-flush :tagspace-offsetint :char-return-pop :string-max :integer-equal? :code-return :vector-nth :vector-flush :integer-return 0.28125 :strings-concat ["À6¤" "ÚMUoÎ\\K{" "Åçi8 " "ÒD" "¥Ö¯¼É"] #{:code-flipstack :refs-rerunall :code-againlater [\+ \ \Á \8 \o \@ \º] :vector-tagwithfloat \Ù :vector-nth :integers-build} :float-sign #{[true true true true true false false] :string-replace [\2 \h \¿ \H \K] ["K¡ÎdÊyZàEÕÉ1°¿5o"] #{:booleans-dup :exec-storestack [] :generator-again (169.8125 [\Ý \. \% \- \½] \ç :booleans-shove :string-containschar?) :vector-do*each [0.1171875 0.0078125]} false :chars-savestack :string-take} (:chars-return :set-union [true false false] :set-yankdup :chars-swap) [3208 3600] :chars-generalize :exec-return :integer-rerunall :integer-print \, :refs->code :refs-nth :integer-dup :set-stackdepth :integer-abs :vector-yankdup]

    :bindings 

    '{:input!2 (:refs-sampler), :input!9 (:string-shove), :input!3 (:booleans-length), :input!10 (:tagspace-comprehension), :input!1 ([true]), :input!8 ([0.12109375 0.06640625]), :input!4 (:integers-return), :input!7 (:floats-flush), :input!5 (#{[0.0859375 0.12109375 0.07421875 0.0078125 0.1171875 0.0 0.04296875 0.0625 0.140625] :integers-tagwithfloat :exec-print :floats-stackdepth :generator-yank 42274624}), :input!6 (true)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

