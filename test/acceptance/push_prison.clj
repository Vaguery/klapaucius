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
              (println (str "\n>>> " (:counter s)
                            "\n items on :exec " (u/get-stack s :exec)
                            "\n>>> ATTEMPTING " (first (u/get-stack s :exec)) 
                            "\n items on :OUTPUT " (get-in s [:bindings :OUTPUT] '())
                            "\n items on :scalar " (u/get-stack s :scalar)
                            "\n items on :complexes " (mapv count (u/get-stack s :complexes))
                            "\n\n"
                            (pr-str (u/peek-at-stack s :log))
                            
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

    '[[\Å \B \& \9] 501867699M ["¼.ºµG|B´" ";" "(X¢`2MM tÐK7" "E¤Ù" "×O%Û®w°ß»F`)¢»^¼" "i©"] :print-empty? :c :boolean-notequal? (["[*ã" "¼Â.-" "}kS-©"] 89/151 #{:set-tag #{:vector-dup :complexes-tagstack 22/61 :set-rerunall [\I \ä \L \Y \space \] :interval-cutflip \: 342387780} :strings-rerunall :scalars-conj :booleans-savestack [0.0859375 0.0703125 0.03515625 0.1328125 0.1171875 0.12890625 0.05859375 0.109375] [3902 4993] (:scalar-divide ("«Yd'7´Ë£äS" :snapshot-begin [3363 66] :complexes-butlast :exec-tagstack) :complex-notequal? :code-atom? :code-againlater)} "âà¬DÅViÁ" :string-liftstack) :interval-swap [false false true false] :scalars-flush :char-storestack :interval-rotate :strings-intoset :scalars-butlast (:scalars-notequal? :ref-echo 500307848 430514501 :chars-conj) :ref-as-set (["§s(Hn" "ªy=v" "P;x±97`á«T\\à.£Ææ«"] [\I \£ \¥ \} \° \· \ \Ý \a] :vector-emptyitem? (518395216M 221.1796875 "T¼ÕU|§" #{#{1/3 :chars-cutflip (759630803M :scalar-againlater :scalars-set :complexes->set :strings-cutstack) #{:complexes-store \) [] :exec->string :intervals-conj [558 2194 1246 4245 471 593 4261 4931 188] :chars-replacefirst #push.type.definitions.interval.Interval{:min 113/119, :max 297.578125, :min-open? false, :max-open? false}} 723423003 #push.type.definitions.complex.Complex{:re 843597993, :im 173.53515625} 409360303 [false false true false true true]} :tagspace-lookupvector :interval-flipstack :complex-scale true :scalar-bigdec? :code-do 829415908M} :strings-tagstack) :chars-empty?) 105922858M :set-intoset :vector-empty? :intervals-first [0.0859375] :intervals-build #{:interval-empty? [\¯ \I \Ë \ \ª \A] [0.1328125 0.078125 0.04296875 0.00390625] (:complex-multiply "A.G¡©¢|SÈÞ¨ãÁÄ#µ»" ["zs2¿]i´¾" "©®Yv¿" "VÒÖ" "w/=X`Åä­" "´~P" "sV,usq¡°²O,dæ²Í×:" "5" "gd¥ Xk>}Üà" "Ìªb{¤ÎaUL­¯ÁàX"] 193/112 #push.type.definitions.interval.Interval{:min 147/178, :max 352.18359375, :min-open? true, :max-open? false}) #push.type.definitions.interval.Interval{:min 38/27, :max 211.29296875, :min-open? false, :max-open? true} #push.type.definitions.interval.Interval{:min 10/53, :max 323.1171875, :min-open? false, :max-open? false} :f :code-atom?} true :interval-add true :vector-items \ false :complexes-indexof #push.type.definitions.complex.Complex{:re 776622974, :im 156.34375} :interval->code #push.type.definitions.complex.Complex{:re 84390405, :im 327.53125} \I [\ \J] :complex-empty? :strings-length :snapshot-dup [\r \ \^ \2 \¨ \È \©] :complex-equal? :OUTPUT :set-superset? :ref-rerunall #{#push.type.definitions.interval.Interval{:min 46/91, :max 18.09375, :min-open? false, :max-open? false} :vector-contains? 47816854M ["7$\"ÉC`7°ØNRÂJÒmË¡" "´áÙåÇ×ÈGI¶{ Ð ¸]" "N ªÍÕY" "m&±C" "ÆL¥;«YÝ'uªMYãMÜ/" "áã" "jnzå¤ÁC@" "Si%Yr²L¼Ì3Ýq^«Çb²"] :refs-byexample (:code-do*times :vector-remove [0.09375 0.1328125] \ #push.type.definitions.complex.Complex{:re 627371625, :im 36.98046875}) 35/61 140141698M} :exec->set :char->string :complex-cutstack false :complex-shove #push.type.definitions.complex.Complex{:re 219108670, :im 263.91796875} :snapshot->code false false ((#push.type.definitions.complex.Complex{:re 33285760, :im 7.28125} (:intervals-flush :d 177/103 #{:code-later :scalar-dup :tagspace-cutoff 234253867 :scalar-flipstack [false] "±/Ê·X?¾§:c¼Öc15¿\"k" :complexes-echo} :complex-return-pop) :char-yankdup :char-store :intervals-nth) :refs->tagspace :d 784283402 \¸) 478583740M 387.03125 [0.125 0.1328125 0.06640625 0.11328125 0.15234375 0.12890625 0.09375 0.04296875] 444412549 ["P¨È>×®rK<^cXFQ" "VdomÛ'"] true #push.type.definitions.interval.Interval{:min 88/181, :max 251.80859375, :min-open? false, :max-open? false} #push.type.definitions.complex.Complex{:re 7374066, :im 180.05078125} 496425790M #{:ref-fillvector :generator-shove :c false "¹¿ä´>B?ËÂt(Æz" [0.0625 0.05859375 0.13671875 0.1484375] [false false] :a} [0.00390625 0.05859375 0.03125 0.11328125 0.1484375 0.14453125 0.07421875 0.140625] false #push.type.definitions.complex.Complex{:re 716934300, :im 24.9609375} false \g 187/49 :intervals-tag :code-member? :chars-cutstack :code-cutstack 694319701M :a ("¦¯" :char->string #push.type.definitions.interval.Interval{:min 19/62, :max 243.35546875, :min-open? true, :max-open? false} [\Ý \] :tagspace-new) :chars-pop :chars-againlater 103/84 :ref-exchange #{:booleans-build 42.7578125 142.1875 #{:boolean-flush 419082868 [] [710 1891 4090 964 103] [0.0703125 0.0 0.01953125 0.06640625] :intervals-portion :tagspace->code :complexes-pt-crossover} :generator-return-pop :tagspace-cutoff "_CXÞ½%V=Zw'Ò¦Ù#" ["Ê" "¸N[k²p¸]" "}Î" "3Æ3" "¦7oasÝ#c}y¹L" "µÚÃsÆP51c[«" "oI;¼Ár¨ÉÀ["]} ["Ý±ËÅ" "ÕÆo~1" "ÜÓ}]¾ju¨" "Ñ0¨}" "5 ¯wÁ5_|ÖÍ)ËÔ" "¢x [flN:*ÅÔ©:"] #{(:chars-liftstack :boolean-faircoin :set-dup :OUTPUT :generator-echo) :exec-flush :strings-return :intervals-distinct [0.1171875 0.08203125 0.01171875 0.08984375 0.015625 0.11328125 0.0703125 0.0625] :snapshot-save :i :string-reverse} ["'X%ÁãÍ0¥O_¯º" "'¥aÔ¿ E " "|i8Ja²'DZ^«Ðw5" "C³Àz³á·Qn)?Ys:JØz" "FÊt" "\\¯&§Jä" "Ð&{<$yQ\\3lUÂ/" "¥Ä&Áº}cÛâISknÉÍ"] :scalar-multiply :string-echoall :code-reduce :chars-indexof false 189/136 :ref-store :b [\¦ \ß \ \Û \j \,] :tagspace-valueremove "vÌJÙ§¤Dm" false 978247134M 437354935M :strings-nth #{:vector-dup #{:string-butlast [] [true true] true #push.type.definitions.interval.Interval{:min 54/25, :max 13.27734375, :min-open? false, :max-open? false} :tagspace-sampler :booleans-echoall} #push.type.definitions.complex.Complex{:re 605850845, :im 300.5546875} :tagspace-pop :complexes-shatter (:complexes-new [0.0390625 0.0078125 0.06640625 0.04296875 0.14453125 0.05859375 0.12890625 0.02734375 0.1015625] #push.type.definitions.interval.Interval{:min 17/128, :max 124.2421875, :min-open? true, :max-open? false} 724925247 [0.0 0.13671875]) :booleans-yankdup :interval-multiply} [] :scalar-min :ref-equal? :code-in-set? (" ~o=)ÓÕË¨Þ" "Î|" :scalars-equal? \$ #push.type.definitions.interval.Interval{:min 32/55, :max 84.08984375, :min-open? false, :max-open? true}) :exec-s true 324.65234375 "¡ã0ÏÞÇlä_¬§Dç" :g [false false true false true false true false false] :set-swap :scalar-abs :string-solid? 114/113 :booleans-set [] [2048 2705 76 1506 1004 824 403 3213] 79/17 :complex-parts 23.19140625 :interval-echoall [false true true true true] :push-bindings 171/130 :string-shatter :scalar-notequal? [1509 99 3600 733] #push.type.definitions.interval.Interval{:min 173/75, :max 268.87890625, :min-open? true, :max-open? true} :set-shove :scalars-replace [\ \7 \Ù] :strings-empty? "aYR«T­2^p(ÛdÅF1v0Ç" [0.03515625 0.09375 0.0 0.00390625 0.01953125 0.00390625 0.078125 0.0390625] :strings-replacefirst :vector-portion :vector-cyclevector :vector-items [4864 4787 1880 1315 3310 1133 3506 1399] #push.type.definitions.interval.Interval{:min 13/21, :max 8.765625, :min-open? true, :max-open? false} [281] [3873 582 3695 1884 3574 4144 3513 568 3491] [false false true false true true false true] [\» \l \a \\ \å] false [] ["çÌªIzçwS)d"] [3576 3260 3956 2765 2128] :generator-yank :chars-byexample :set-empty? :scalars-byexample "³Ù¹°ª;smÕRÛ§4<a(" #push.type.definitions.interval.Interval{:min 47/12, :max 193.5859375, :min-open? true, :max-open? false} :push-nthref :ref-return-pop :tagspace-keyset :string-replacechar #push.type.definitions.interval.Interval{:min 3/4, :max 209.23046875, :min-open? false, :max-open? false} :d :refs-butlast :complex-parts false :complex-echoall 48015686M :code-extract :set-intoset [] :scalars-dup :scalar-max :scalar-as-set 2 :refs-vremove :push-quoterefs :refs-occurrencesof \o :booleans-take :string≤? :strings-length #push.type.definitions.interval.Interval{:min 10/11, :max 139.9453125, :min-open? true, :max-open? false} 973395378 360.51171875 #push.type.definitions.complex.Complex{:re 710827820, :im 195.875} :complexes-print #push.type.definitions.interval.Interval{:min 2, :max 116.9375, :min-open? true, :max-open? true} :scalars-shove [4775 4204 2481 758] :complex-norm :interval-make \½ ["A"] :boolean-flipstack :f \q :scalars-replacefirst [false true false true false false false] :refs-indexof]



    :bindings 

    '{:OUTPUT (), :e ((:chars-storestack 142.81640625 :set-swap :exec-as-set 758348727 [\J \~ \Ê \£] #push.type.definitions.interval.Interval{:min 21/32, :max 366.07421875, :min-open? false, :max-open? false} :char-tagstack :set-flush)), :g ((:vector-refilter #push.type.definitions.complex.Complex{:re 599297462, :im 175.46875} :ref-rotate [\1 \. \V \N \` \¾ \] [] 748100549 "¤¢+çb" 685679853 [0.06640625 0.0234375 0.0859375 0.015625 0.11328125 0.0234375 0.12890625])), :c ((:interval-echo false 125/74 226333772 [\q \Ý] :chars-savestack :tagspace-valuefilter :refs-pt-crossover #push.type.definitions.complex.Complex{:re 887539061, :im 46.65234375} #push.type.definitions.complex.Complex{:re 548016169, :im 31.73046875})), :j (([0.05078125 0.01953125 0.04296875 0.01171875] :chars-intoset 371.87890625 :complexes-cyclevector [823 3457 1902 4435 4523 3146 3388] [2736 297] false :char<? :string-flush 114465997)), :h ((:booleans-dup [0.1171875 0.14453125 0.1328125 0.125 0.0625 0.06640625 0.15234375 0.0859375 0.06640625] :ref-dump-tagspace [false true true true false true true] :generator-return-pop ([true false true true false false false] :complexes-vsplit [3138 649] :boolean-later) false :booleans-flush :complex-multiply)), :b ((#{:vector-items :set-rerunall 691101195 :scalars-rest [0.0 0.10546875 0.0625] :booleans-tagstack :vector-distinct :complex-echoall} [\Æ] 962383064 \/ true [\] \h \Q \G \Î \t \X \? \6] 353.19921875 :strings-tag (["ÉÄiHpx" "wµÅÞ+ÁYÙ]>à" "'B;:" "¼"] #push.type.definitions.complex.Complex{:re 630072047, :im 387.59375} 228.19921875 #{["Z¥%s 7Õ" "Ê¢Ñ¾)¤d´ÙÜk" "W´GS&ÒbxC$YÏ¼1â0j" "¤°Ð$tÊÙ" "®pF_ÛÞ8ÛZ.QÉ" "¿ÎÐÖÏRß%¾ä3"] #{#{394855192 :strings-empty? :vector->code false :scalar-shove #push.type.definitions.interval.Interval{:min 10/41, :max 222.9375, :min-open? false, :max-open? false}} :code-quote :snapshot-cutflip [\i \A \¨ \C \ \- \É] #push.type.definitions.interval.Interval{:min 22/185, :max 22.23828125, :min-open? false, :max-open? false} [false true true true false true true false true] :intervals-return-pop false} :interval-swap #push.type.definitions.complex.Complex{:re 914850980, :im 225.4296875} :tagspace-yankdup :strings-flipstack}) [0.0390625 0.0625 0.03125 0.09765625 0.05078125])), :d ((148193893M "±f»QDVËX" [4060 2236 1301] 4 :complex-parts :code-equal? :string-rest 43/187 :snapshot-yankdup 887089237M)), :f (("çàA¿¾Þæ)" [] 941893724 :scalar-savestack :complex-cutflip 55.7890625 "ÄµFP" :refs-vsplit :refs-generalizeall [2056 3319])), :i ((:complex->code [0.1484375 0.01953125 0.1484375 0.0234375 0.0546875] :strings-cyclevector [true true true true false false] "£ÈqÍ" 716308539 [\È \± \A \Ç \Ã] "m×ÞvµYye¹}Õ8ÀC#" :ref-dup :code-save)), :a ((:integer-totalistic3 :vector-last :ref-empty? \z :scalars-sampler \6 :tagspace-tag "eÈEÜs+ÉÈnÕhØ¹=v" #{:tagspace-swap #{[0.08203125 0.046875] :strings-echo \ :scalars-yankdup ")¬3²ÇÄI¢ªàM½bº" :code-points 350702036 :scalars-nth} 807666828 true :boolean-xor :boolean-arity3 #{:set-empty? [] [284] :snapshot-tag #{["6Â¹Td×YYmªB=£~" "á¯IaÝ-0HØ" "" "eiUS]AXsUÍÝ`x" "ÆvàmÚ7+HT§Q4q" "ÚÏ¿]å¥#@áFU{ÂFÀ\\"] [] true [0.1171875 0.0859375] :ref-tag 196/29} (:push-refcycler :boolean-echo :ref-in-set? true [2679 4910]) :code-atom? :scalar≤?} :interval-echo}))}


}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

