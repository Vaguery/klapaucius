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
                            "\n items on :tagspace " (u/get-stack s :tagspace)
                            "\n items on :complexes " (u/get-stack s :complexes)
                            "\n items on :scalar " (u/get-stack s :scalar)
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

    '[150.66796875 (:scalars-vremove [] (true [0.03515625 0.05859375 0.1328125 0.05859375 0.0078125 0.11328125 0.08984375 0.0078125 0.140625] [] :integer-totalistic3 true) #{#push.type.definitions.complex.Complex{:re 417018983, :im 73.67578125} :chars-store #push.type.definitions.interval.Interval{:min 34/29, :max 267.41015625, :min-open? false, :max-open? false} :booleans-fillvector [] ["+²dçpÜ* 9ãBvÉ" "c*E­{Éµ&Gµ7³À2Þ¬Ñ" "¤²_M |Ä\"" " ¶y('ËÔÍE ã<T" "b.xÒ¯sL{ÝUQ" ">x3És.y>¬oÊaÍ" "EÔPZAV_U/1;KSV9QÛ"] :scalars-dup :complexes-rest} :chars-portion) 6/49 638111606M :interval-liftstack :exec-if #push.type.definitions.interval.Interval{:min 1/81, :max 57.45703125, :min-open? true, :max-open? true} [] 672303127M :set-cutstack "Ww6c" [true false true true false false false false false] \S "È!J¨»5" :set-echo (false :generator-liftstack #push.type.definitions.interval.Interval{:min 57/5, :max 160.03125, :min-open? true, :max-open? true} :complexes-tagstack true) :complexes-rerunall :strings-items \k :chars-generalize 174/35 #push.type.definitions.complex.Complex{:re 203620188, :im 198.49609375} [0.046875 0.078125 0.0078125 0.08984375] :intervals-pt-crossover #push.type.definitions.complex.Complex{:re 733554828, :im 244.41015625} (#{#{:boolean-dup :set-union [] 11/103 :refs-savestack :generator-again [\¯ \ \Ö \] \m \Ì \Ü \ \)] :vector-vremove} :exec-echoall :input!3 :input!8 :push-cycleARGS :string-cycler :string-yank 931012801M} :input!7 959433220M :booleans-echoall :refs-cycler) false [""] :booleans-take true #push.type.definitions.complex.Complex{:re 653451225, :im 331.296875} :set-stackdepth 712229795M :vector-vsplit :vector-new ([] false [2009] :ref-store :refs-cutstack) :scalars-last [\³ \? \ç \ \º] :vector-yank :ref-new [false] 5/142 :exec-rotate (:scalar>? [0.046875 0.1328125 0.09375 0.0859375 0.05078125 0.15234375 0.09375 0.12109375] :ref-new :push-cycleARGS :complexes-againlater) :input!5 #push.type.definitions.complex.Complex{:re 321139830, :im 254.97265625} :string->set 311.8984375 [\­ \W \j] ["~à" "K.=QlËÁ" "Rn¦»k«ãÈÜ Ä" "ÏÒExl" "71ß¼7¼?#gµÀ" "{?Î" "®E2ÅO§äwIå&b?åP±" ","] :refs-swap 703215637 135245677 :ref-tag [true false true false true false] [1739 734 3346 3987 2202 4695] :exec-s :scalars-againlater :complexes-indexof :complexes-do*each #push.type.definitions.complex.Complex{:re 739689316, :im 211.83203125} ([2286 1462 2846 976 4966 4348 2679 2178] \0 (:intervals-yank [1979 3457 937 1369 447 1368 370] 2.7578125 26657364M :scalars-rest) :complex-tagstack :ref-cutflip) :generator-tag \l :complexes-stackdepth :vector-new #{:char-notequal? [0.07421875 0.109375 0.11328125] :interval-tag [] 137/164 #push.type.definitions.complex.Complex{:re 949316555, :im 360.79296875} 755834430 :interval-cutstack} :snapshot-stackdepth #push.type.definitions.interval.Interval{:min 133/34, :max 126.8671875, :min-open? false, :max-open? false} #push.type.definitions.interval.Interval{:min 55/162, :max 280.375, :min-open? false, :max-open? true} false :char-flush [342 4643 4018 4] #{159.17578125 :scalar->code 257912794M 137/199 :scalars-conj [false false true true true] 195/112 [0.1484375 0.05078125 0.1484375 0.03125 0.140625 0.13671875 0.0390625]} :vector-concat [0.04296875 0.1484375 0.01171875] ["IháÉ¾3{P" "Ä ¹T7/nÈ~n7D·/"] [0.03515625 0.13671875 0.0625 0.08984375 0.03125 0.01171875] :scalars-rest 938668543 :strings-rerunall :scalars-vsplit :push-bindingset [1488] :vector-remove :char-stackdepth :interval-hull :refs-replace :complexes-rest [1762 3942 2202 1584 2522 2798] :string-conjchar #push.type.definitions.interval.Interval{:min 2/5, :max 41.40625, :min-open? false, :max-open? false} #{:tagspace-cycler :strings-nth #push.type.definitions.interval.Interval{:min 74/27, :max 198.94140625, :min-open? true, :max-open? false} (:chars-build :refs-generalize :refs-new :interval-rotate #push.type.definitions.complex.Complex{:re 226162491, :im 25.62890625}) false :interval-cutflip [3186 3366 610 1290 317 2705 1896 4803 2878] :input!5} true :generator-againlater :scalars-dup [0.12890625 0.109375 0.01171875 0.13671875 0.1171875 0.12109375 0.08984375 0.1171875] :scalar-empty? :complexes-flipstack :chars-byexample #push.type.definitions.complex.Complex{:re 933378276, :im 47.78515625} :boolean-stackdepth true 22270709 :strings-fillvector :snapshot-begin (:interval-intoset :scalar-print true :set-equal? :strings-as-set) 894373265M 361338677 :scalars-butlast :scalar->asciichar :scalars-distinct 509618186M :vector-rotate :input!10 :intervals-rest 309656947 :ref-exchange [2530 4843 1695 2157 4462 4938 4742] #{:string>? [false false true] [\ \$ \¼ \ \" \Ç \¬] 76664182 "º¿ØÓâ\"75Å5" 33/46 ["^°"] #push.type.definitions.complex.Complex{:re 316344899, :im 219.23828125}} "56lu72|ÑPÆKæg³" :boolean-swap :intervals-occurrencesof :vector-length [0.03125 0.04296875 0.15234375 0.0546875 0.1171875 0.0546875 0.109375 0.0078125] 199.4921875 :intervals-yank :complex-storestack [] :vector-comprehension #{304.640625 [true false true true false] [true false true true true] :scalars-split false :booleans-nth [0.10546875 0.0859375 0.08984375 0.125 0.0546875 0.0625] :strings-contains?} :input!5 :tagspace-normalize :strings-tagstack [] :ref-fullquote :ref-yankdup [false false false true true false true false] "1#¢LÓ-¬£³À_cÝ" [false false false true true false false true] :input!5 :exec-cycler :ref-yank :ref-ARGS 516232384M :complexes-empty? #{:input!2 :code-insert 345.48828125 [\ \ \n \ \ \] \s :strings-intoset :refs-yankdup :code-container} :interval-return-pop :log-empty? 799757379M :vector-liftstack :refs-occurrencesof :input!4 #push.type.definitions.complex.Complex{:re 491356033, :im 374.45703125} \§ 81/74 61/90 [357 3212 4568 2515 2647 3201 3351 4587 2042] (["ÉOºÓ@>IÚ×²!" "AÆ¬Þ{¸ÏX" "Üj>Q]°#"] :scalar-flush :tagspace-offset :string->tagspace 18.9765625) :tagspace-tag :scalar-dup [\ \Ì \ \k \a] :tagspace-max :string-sampler :scalars-take [0.10546875 0.109375 0.09375 0.08984375 0.14453125] 289211103M :ref-in-set? 214059849 :vector-yank #push.type.definitions.complex.Complex{:re 806023498, :im 354.6484375} :string-sampler :complex-divide :interval-savestack :chars-rest :boolean-stackdepth :scalars-stackdepth :chars-empty? :intervals-yank :chars-last :set-equal? 213.19921875 18/17 :string-rerunall [2364 1329 2187] 64947833M 554241655M :intervals-conj-set :refs-yankdup 15.99609375 :strings-echo :boolean-cutflip ["OÉK7ØåxZWÄ" "°0ªß]ØW­¶§" "³0³{Ì°uh M«n 4\\" "aÆ" ">t±i([ªkK" "bHu\\Yv¥ÉP1¬°"] true #push.type.definitions.interval.Interval{:min 43/19, :max 288.703125, :min-open? true, :max-open? false} :strings-comprehension 399092249M [2773 1640 586 3383 438 661 1558 2001 4413] :ref-fillvector :intervals-tagstack]



    :bindings 

    '{:input!2 (:strings-contains?), :input!9 (:interval->set), :input!3 (:boolean-storestack), :input!10 (:snapshot-yankdup), :input!1 (#push.type.definitions.complex.Complex{:re 512871119, :im 340.99609375}), :input!8 ([216]), :input!4 (["x¦JÍc" "K" "1Ç6" "RÆÏºÛ²°Sµ¤h2k" "-Ç" "qº»ÓKËE},°uX¥4" "#&Ù" "TÅ?&LºXÚ¥"]), :input!7 (#{[0.125 0.00390625 0.13671875 0.11328125 0.10546875 0.0078125 0.0390625 0.046875 0.046875] [1693 728 3647 2124 3695 4316 3000 1621] :strings-echoall :scalar-subtract :strings-indexof :interval-multiply :booleans-contains?}), :input!5 (#{:scalars-intoset #{:scalars-filter [true false] "«SÐ_R¼hv{l®!zÓ/" :vector-storestack #{[0.15234375 0.08203125 0.01171875 0.0234375 0.02734375 0.046875 0.1015625] [] #push.type.definitions.interval.Interval{:min 188/113, :max 137.0390625, :min-open? true, :max-open? true} :scalar->string 634075001 :chars-save [false] :snapshot-tagstack} :strings-sampler :strings-vremove :code-flush} :chars-take :ref-echo [] #push.type.definitions.interval.Interval{:min 10/9, :max 144.8515625, :min-open? false, :max-open? true} (["¼¹Àt>}" "â¼Ú²" ")J" "H¨LA¾ÒÓg2*[Ñ" "°ca³]Ô" "qÇÎÃ³ÔÒo$$)¸wKU²" "£Öß4" "mp"] :complexes-vfilter :scalar-E :strings-as-set) :scalar-yankdup}), :input!6 (119/95)}


}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

