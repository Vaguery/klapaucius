(ns acceptance.push-prison
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.type.core :as types]
            [push.util.stack-manipulation :as u]
            [push.util.code-wrangling :as fix]
            [clojure.string :as s])
  (:use midje.sweet)
  (:use [push.interpreter.core])
  (:use [push.interpreter.templates.one-with-everything])
  )


(defn overloaded-interpreter
  [prisoner]
  (-> (make-everything-interpreter :config {:step-limit 20000 :lenient true :max-collection-size 138072}
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
              (println (str ;;"\n>>> " (:counter s)
                            ; "\n items on :exec " (u/get-stack s :exec)
                            ; "\n>>> ATTEMPTING " (pr-str (first (u/get-stack s :exec)))
                            ; "\n items on :OUTPUT " (get-in s [:bindings :OUTPUT] '())
                            "\n items on :scalar " (u/get-stack s :scalar)
                            ; "\n items on :return " (u/get-stack s :return)
                            ; "\n\n"
                            ; (pr-str (u/peek-at-stack s :log))
                            
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
  
  {
    :program 

    '[:booleans-pop \ 899300736 \ :exec-equal? :code-return-pop #push.type.definitions.interval.Interval{:min 133/162, :max 331.08203125, :min-open? true, :max-open? true} :intervals-generalize :char-as-set \0 :vector-print 802246192M false #{#push.type.definitions.complex.Complex{:re 830429155, :im 143.53515625} :exec-as-set 938418139M :char->set :tagspace-rerunall :string-indexofchar :ref-swap :scalar≤?} :complexes-nth :boolean->set (:booleans-replacefirst [false] ["Tã{ Ú«" "¢(F%06"] :generator-pop 351.43359375) [0.08203125 0.07421875 0.11328125] ["¦Ù\\)" "¯J|Ð)iâÊ" "ßXU'xÆ}ÑP" ";o:g ¡f`Ïhd" "W£Yfw1¢" "ÁzÒ©M³¾!k`9ªNÞ?Æ"] :booleans-empty? 37/36 :strings-notequal? :exec->string :interval-return [3401 4520 4075 4968 2195 1339 2432 1316] #{#{252.6640625 :booleans-length :scalars-byexample :scalar-fractional :string-storestack :complexes-pt-crossover [0.09375 0.046875] :booleans-generalize} :interval-subset? :complexes-distinct 623219292M [\? \Þ \w] #push.type.definitions.complex.Complex{:re 950097725, :im 71.0390625} [4305 3760 4300 3726 3224 2299 431 810] #push.type.definitions.complex.Complex{:re 725043050, :im 305.97265625}} ["²k8&" "pmusqq$B½«];r" "ÎzvÅ%¢ " "eÚF«Ç&^¿ÜCZµ;ÊJÂ" "?µu'" "3ªÆæ:#!¹£_)1fÆ¯T¿" "E2ÙBN[Å.Ègw+¶áç" "ç§Ä§GfWmá%" "´¼mã.©¿-Ã$¸[ª"] :set-difference ["-¨±Vs£°¯ÅÁçÀ" "ç§Â¹:©we!AZ½" " °Ø©aµ-GzÉl´Îq-×{" "<1[Òä¦©nyÄe³,b" "I'2å¤_ÁÄÁÝ¸" "ÆÛ" "Ì»=©8jS©à3&s|" "Ì¦QàØxÝ¾ÀM¢Pß^"] false #push.type.definitions.interval.Interval{:min 77/46, :max 125.33203125, :min-open? false, :max-open? true} :c "O" 369.453125 :booleans-occurrencesof "æµK¬ÍÍÉEtF" 72177436 948385086 :code-intoset "å¦NW¦i" (:vector-refilter :scalars-contains? true #push.type.definitions.interval.Interval{:min 117/41, :max 282.58984375, :min-open? false, :max-open? false} :scalars-remove) :chars-byexample "@P*k?º+Å´Zt­¼b,*7" [0.01171875 0.015625 0.10546875 0.01953125 0.05078125] \x :b :snapshot-begin #push.type.definitions.interval.Interval{:min 66/13, :max 218.7578125, :min-open? false, :max-open? true} :intervals-fillvector :exec-flipstack [true true false true true true] :ref-ARGS [0.125 0.1015625 0.03125 0.015625] :tagspace-flush :string-equal? #push.type.definitions.complex.Complex{:re 949254278, :im 190.64453125} 953358360M ["2" "'ØC|oÙÁ¸MZ.-®8ºx" "Øf)j"] [588 2078 4323 1902 3496 2098 2012 3808 4406] :scalar->boolean (55445132 153861628M :refs-yankdup "ÏÓIR._¨àrJuÝP" #push.type.definitions.interval.Interval{:min 47/11, :max 277.11328125, :min-open? true, :max-open? false}) [\t \y \ \M \§ \`] 201.18359375 (:snapshot-tag :i [] :string-intoset :ref-echo) [false false true false true] [0.07421875] ["*;M" "°o A»w+" "3·Yr(" "tÛD¤$m\\ã^IÇÉ±Ù'" "vc;x^u" "#4Ê\\/ËÛ-É/Iu¶Miã" "DÓ>dælÚÍÜ(" "q¯%nuÌ5À" "n±å¸"] :char-echo true (:refs-fillvector :booleans-byexample :booleans-cutstack 180206783 :refs-occurrencesof) ["¦màáÛ_&Ö×nâä " "avF$³¨`Ç3o¤~Ý-`Tw" "ä±¤FÈKZne9m"] [false true true true] :complexes-later :code-notequal? :complexes->tagspace [0.08984375 0.0859375 0.0234375 0.12109375 0.109375 0.15234375] #{147.515625 :ref-dump #push.type.definitions.complex.Complex{:re 942848113, :im 145.50390625} :strings-nth :snapshot-flipstack :scalar-cutflip ("Ø_LX¤¢1fãXl4´I" #{[false false true] :char->string :interval-intoset :g :generator-reset :code-noop :complex-empty? :a} [false true true] "LR}¶" "ªBV£SE") "|$¯À®ÖD·±?N[FO+BÙi"} 40.73046875 [\ \D \ç \I \³] :interval-empty? #{:set-cutflip [false true] :char-yankdup :g :c [\º \å \Æ \ \ \9] :strings-take [false true true true true true]} :string-return :scalar-rerunall ([true false false false false true false] :scalars-vsplit :tagspace-tag :generator-liftstack []) :g \¬ #push.type.definitions.complex.Complex{:re 894682940, :im 14.625} \f (["7w¦­ås)²áã?ß´´EBÅÎ"] \ :refs-shove #push.type.definitions.complex.Complex{:re 545139074, :im 275.91796875} :intervals-vfilter) :ref-echo [2599 2762 3595 3342 265 2968 1531 1763] [] :scalars->set \ ["àDB#Ýp©±z-F§ªQ-" "¦·G¿ÖOCÀ"] true [false false true false false false] :char->code 27/37 :complex-cutflip #push.type.definitions.interval.Interval{:min 89/36, :max 255.41015625, :min-open? true, :max-open? false} "k¹¶Çj­I^H­¬ÆpÖNQ`Ý§" #push.type.definitions.interval.Interval{:min 143/128, :max 315.35546875, :min-open? true, :max-open? true} :chars-flush "Yç&(ÇyO" [\ \f \ \[ \ \; \Ú \w] 178.6328125 :interval-make false 991577420M :complex-tagstack :tagspace-lookup 550793779M :booleans-build :a :snapshot-begin [822 1450 2328 3387 2997] :set-sampler #push.type.definitions.complex.Complex{:re 6813784, :im 206.0703125} "¯háÚæÅQÎd]" :scalars-replace :strings-return-pop "Hr5Ú8y¸NºÏ" :booleans-flush false :code-dup :code-position :intervals-shatter \Ø :ref-rotate [\6 \- \2 \² \= \·] :complexes-take false :string-spacey? #{477948413 \« 71/140 true 298.86328125 :g #push.type.definitions.complex.Complex{:re 959179022, :im 19.125} #{:chars->set :vector-print :tagspace-later :chars-in-set? 313.80078125 383.55078125 :vector-echoall :vector-later}} 52.9453125 497033744M :scalar-return-pop [">³ÇC×¯(4K±u[Ê¶Õ" "Þ²4âBU+^64M3¯" "%?<oËDfl£mU'r" "6ÊEg"] #{77/71 :code-rotate :strings-return-pop :h :refs-yankdup 73/28 #push.type.definitions.complex.Complex{:re 474158532, :im 177.95703125} :code-do} [0.01171875 0.0078125 0.03515625] [\ \¬ \Â \ \M \U \w \³ \¿] #{#push.type.definitions.complex.Complex{:re 240988902, :im 307.40234375} [] 506173621M :intervals-replace :intervals-equal? :interval-make :vector-tagstack :boolean->integer} (814229164M (:tagspace-later true #push.type.definitions.interval.Interval{:min 3/5, :max 24.109375, :min-open? false, :max-open? false} :chars->tagspace true) :set-storestack :j :vector-liftstack) [0.1328125 0.11328125] 53/19 \ :vector-cutstack #push.type.definitions.complex.Complex{:re 315498414, :im 131.9375} :integer-uniform :scalar-infinite? "m«Âçz t~ßQÚ¸²Ô" :tagspace-flipstack :vector-distinct :intervals-stackdepth :complexes-contains? [796 3579 4272 3049 747 1994] [\8 \¤ \] 753086069 :ref-cutstack [0.07421875 0.140625 0.125 0.08203125 0.0234375 0.140625 0.14453125 0.1484375 0.07421875] :booleans-tag :tagspace-split (43/41 472933962 96873220M :scalars-comprehension :strings-dup) 873586171M 202089107 false [\ \C] "´³\"`" :scalar-flush ["Ô³¸(iÃÈ²y1¶[|k" "%TÅ¯­¡ÖËvÚ²m¬Nj¹Â" "<BªqrE3¬" "nT¥:ß.>" "-!¤&¶6ÂFÂX" "jµXµÛ0±JQ p¥" "çU´c0<Ë½X®GcåÏ3"] :c \Û \É (:intervals->code ["FÏyMtä3Ó0" "`" "Ù°°ÜåEÏÆ»&ÁVYÄ+o¦" "¶DQRHhI]­ÖÃ" "RmËÔ,9À9ËÃª¤ns" "¡4S+À"] [0.08984375 0.04296875] [0.12109375 0.09375 0.1484375 0.0 0.0390625] :booleans-pt-crossover) :complexes-conj :strings-pt-crossover :scalar-add 278586595 [\ \£ \R \Õ] :scalars-tag :complexes-cycler ["6KR\\Í" "aE9¨-a¶!ÌÉRá~§v"] [] #push.type.definitions.interval.Interval{:min 7/19, :max 261.63671875, :min-open? false, :max-open? false} 576319842 [true true false] :booleans-concat ["Y" " o=zµO)Z¬waO1/" "ª:o°" "EÜ" "¶´VÎF6r" "´gæ~thEÏ" "vÛÝj®-G" ":×;°h!" "15»y&¦VÎJ£-?´"] 91/113 277.47265625 :exec-string-iterate :scalars-liftstack true false ["ÆU9á¥ÝºO" "'HÙ®©,Aª¢" "k." "" "Þ¬Ô" "v" "_IQ r½Ö#Ì!"] :refs-length [\, \Ï \¨] :boolean-echoall \]



    :bindings 

    '{:OUTPUT (), :e (((false #push.type.definitions.interval.Interval{:min 122/75, :max 303.24609375, :min-open? true, :max-open? false} 250.546875 [0.13671875 0.0234375 0.11328125 0.1171875]) :scalars-do*each false false [" ÑËB°ª" "G¢âãÈÜ.-â&­fl}" "a¯F'tq5ÓX<"] :complex-conjugate :code-append :set-flush false \Z)), :g ((:interval-include? :booleans-length #{116.04296875 :boolean-equal? 766975258M 592141459 :booleans-remove 62/83 "s«gFX²¾.=" [true false false false true false true false]} :string-liftstack [\Ð \< \@ \ ] :exec-s 40164437M :tagspace-save :tagspace-min :generator-reset)), :c ((:intervals-new 672897750 #{:chars-replace #push.type.definitions.complex.Complex{:re 969631604, :im 199.3515625} :char-return 647395322 true 959383724M [\space] :scalars-savestack} :booleans-portion [0.01953125 0.03125 0.03125 0.0625 0.04296875] :refs-replace :string-tag :ref-equal? :set-echo #push.type.definitions.interval.Interval{:min 188/59, :max 290.7421875, :min-open? false, :max-open? false})), :j ((#push.type.definitions.complex.Complex{:re 234693697, :im 132.46484375} 767389167M 101883890M :ref-new "Ð¸¶" :string-cycler [0.04296875] :set-dup)), :h ((100462571M "}>ÄÍÚÀÆª­zÜMVá·Y" :ref-conj-set (true true :char-liftstack :string-tag ["IrK¨»" "»²¤Ú!" "ZÜY" "Ìi£;C¨Îª­" "®p¬`x" "8X±ÞSRZYW" "$Ø§Q;ÀÈ"]) :push-bindingcount :interval-overlap? #push.type.definitions.complex.Complex{:re 336985437, :im 228.4921875} :float-uniform [false true false false true true true true true] [true false true true true])), :b ((#{:interval-liftstack :exec-storestack :vector-equal? :ref-peek ["¿È4`µÙ" "Í³" "h'gUÝb" "­" "Í2ÎÑ&S¹8­b¸Å"] false ([0.1171875 0.15234375 0.04296875 0.09765625 0.1015625 0.07421875] #{:booleans-build "Fr\"Û®g 6\\3" :string-swap #push.type.definitions.interval.Interval{:min 67/78, :max 209.1953125, :min-open? true, :max-open? true} [\y \Æ \p \© \) \, \ \Ô] :complex-multiply :complexes-swap [4138 4807 4251 4002 3626 864 3739 790]} :complex-stackdepth 462748995M \4) :interval-return} :code-map 963186484M [\k \ \å \Æ \â \Ä] :strings-indexof :vector-pt-crossover [] :exec->string :scalarsign->boolean [3586 2368 4517])), :d ((#push.type.definitions.interval.Interval{:min 43/27, :max 284.16015625, :min-open? true, :max-open? true} :booleans-shatter :string-notequal? \\ :intervals-contains? "QzS*|S:b¾àÛ·ÌIDeå" :refs-generalize :char-storestack [\« \])), :f ((:vector-tagstack :scalar-ceiling :boolean-later :interval-parts :booleans-empty? :code-position true 529460031M 369886695M :vector-rerunall)), :i ((#push.type.definitions.interval.Interval{:min 16/5, :max 279.73828125, :min-open? true, :max-open? false} ["¯Ó¬ÈÌ1{hgV£{A]Út" "mÂz¯¢ `¶NXx½"] :booleans-butlast :vector-tag 497278896 \i "=7j¢Â1Ø¬;0hXh" :boolean-3bittable :vector-fillvector)), :a (([true] :ref-storestack :generator-cutflip [false true false true false false true] "ÊE6F­Ê" #push.type.definitions.interval.Interval{:min 48/25, :max 45.97265625, :min-open? true, :max-open? false} :interval-recenter [false false true false false false true] [0.08203125 0.1328125 0.09375 0.0390625 0.08984375 0.078125 0.1171875 0.09375] :string-replacefirstchar))}


}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

