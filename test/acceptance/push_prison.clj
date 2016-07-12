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
                            "\n items on :generator " (count (u/get-stack s :generator))
                            "\n items on :vector " (u/get-stack s :vector)
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

    '[9/7 #{:vector-byexample :string<? [false true true false true] false :intervals-rerunall (209.6875 "£­¤(VpÄ" :scalar-tag [3108 1446 4236] :interval-rerunall) [\´] "ä[&¨ÕYe¸×¼Ç"} [880 1053] 147862633M :strings-last :code-shove [0.05078125] \ :intervals-vremove :input!6 :scalars-items :chars->tagspace :char-yankdup :strings-butlast ["C.ÄÜ" "k{¾UÏx<v:5A3ÖW" "@vÈ+UÌÔGÈ" "ÖÏ_Êk­@Ó¬"] :scalar-arcsine ("8­Ô©¯ÉkB" ["´ã¸h> (,~~¼ovhB" "sÅT'" "O*5Æ«²ª¢Ð" "" "W/ª¥(P;>Ñ®~uÞlÏÓ" "xÝp" "@ÐÐE1µ®x¿²´K_ßm>·á" "ÚU;XÈà¯¢hLa!"] :set-save "±p½çB}4L­2¿Ê'" :refs-tagstack) :refs-contains? ["À8¤NÈÃ}=" "..Y*\"*8" "<Ü 5\"" "­5,Ùt" "<qF¡t­dß¶qrkzR~¦«" "2QÂVßÚ" "<PÊ³gÁ"] :chars-tagstack 552402358 #push.type.definitions.interval.Interval{:min 68/135, :max 68.23046875, :min-open? true, :max-open? true} :chars-vsplit :chars-last :scalars-replace [3536 3483 4962 494 191] "#¿r*/KÜ±\\£Ü" :complexes-take 514282094M :vector-comprehension :interval-notequal? :code-do* 252.78125 :tagspace-valuevector \m :scalars-split :booleans-in-set? [false false true true true false false true] :generator-tagstack [true true true true false] #{467726690 #push.type.definitions.complex.Complex{:re 350402074, :im 63.68359375} #push.type.definitions.complex.Complex{:re 575439842, :im 24.390625} true 86798981 #push.type.definitions.interval.Interval{:min 85/194, :max 279.921875, :min-open? true, :max-open? false} [false]} :scalar-power :booleans-take :chars-swap 267.23828125 :refs-new [\ \i \b] :set-dup ([0.046875 0.0 0.140625 0.0390625 0.00390625 0.08203125] :complex-pop 225237337 [false false false true] "ÞªK+6N¼¢*Þ") :strings-tag :input!8 [false true false] 1/36 :complexes-byexample [] :push-quoterefs (#push.type.definitions.interval.Interval{:min 62/47, :max 4.43359375, :min-open? true, :max-open? false} 8/39 :string-return 887772973M 39/7) (#{[0.06640625 0.01171875] :interval-overlap? [] #push.type.definitions.complex.Complex{:re 759240461, :im 12.9453125} #push.type.definitions.interval.Interval{:min 64/109, :max 306.96484375, :min-open? false, :max-open? true} :refs-concat :tagspace-lookupscalars [\ \­ \U \space \l \a \ \Á \]} :booleans-length 452458684M :strings-in-set? [true false false true false true false true false]) :intervals->code #push.type.definitions.complex.Complex{:re 316621526, :im 140.90625} 810950006M #push.type.definitions.complex.Complex{:re 170444439, :im 205.41796875} true :intervals-yankdup #{([1646 1150 3748] :exec-conj-set [0.1328125 0.0859375 0.03125 0.02734375 0.1171875] :intervals-replacefirst ["å°[4gy×" "2ÈL¨,^®" "b:3Ækd}DÔ¿q5ËmSF" "{©Ü\"ç5º'l¡ã'" "%çqª9Ï©µ{`ZhEËrE±U" "ËÊÃ(ã(`c¥ºvmÎ"]) #{292.15625 :intervals-againlater #push.type.definitions.complex.Complex{:re 810829757, :im 115.71875} :generator-pop "Wi" :complexes-indexof 957242819 "bc¥¹{Í[Ò"} [0.12109375 0.078125 0.0390625 0.13671875] "Á¦¤A¤" [true false true false true true true false] :intervals-rest "ÃÄÂ`" :vector-vremove} 698264507M :chars-flipstack :chars-in-set? :intervals-replacefirst [0.03515625 0.15234375 0.1328125 0.11328125 0.08984375 0.1015625 0.05859375 0.03515625 0.05078125] [892 2501 4024] :ref-flush 104.01171875 [0.0859375 0.14453125 0.0546875 0.09375 0.01953125 0.0390625] [true true false false false] :input!6 :interval-return [139 3706 4034 4503] :char-echo ["%qfv6/?jÐG" "Ð" "§79$@#äTÜro)ÇÒ×" "Ê" "ªJ¶" "¾"] "BÏ°j½WsjØ)C3\"Ö6Å" :tagspace-dup (:chars-flipstack :intervals-shatter :interval-rotate :scalar>? 68/11) :scalars-stackdepth 984842544M :vector-fillvector :string>? :input!5 \W [0.04296875 0.0546875 0.09375 0.09375] :strings-flipstack :intervals-length #{#push.type.definitions.interval.Interval{:min 110/191, :max 224.92578125, :min-open? false, :max-open? false} :strings-save #push.type.definitions.complex.Complex{:re 751157240, :im 111.66015625} #push.type.definitions.interval.Interval{:min 174/77, :max 45.703125, :min-open? true, :max-open? true} [0.140625 0.04296875 0.1328125 0.04296875 0.08984375] :complexes-first :scalar-float? :complexes-portion} [3588 2140 4512] "ZÐY'åYÑ?!ÐÓWØ°ÁÓL±]" :ref-ARGS :tagspace-new :intervals-empty? :chars-contains? :intervals-fillvector :exec-do*times (:set-later :complexes-cutflip 43.76953125 :string-dup ["È" "ÛZqN¶*d)¦ÛÐ`«" "ªmÛç]F×¸bFd" "hÝÊxª" "§8&Ë&yÃEÖGÉ" "ÅAâ" "!qÍÆ¯ç)×:Á" "M+©·QX£"]) :complexes-new :scalar-echoall :booleans-savestack :generator-flush :strings-storestack true "Ñt¦C" #{132.57421875 #{142.1328125 #push.type.definitions.complex.Complex{:re 601746151, :im 264.9140625} #{[false true false false false] #push.type.definitions.interval.Interval{:min 91/18, :max 132.07421875, :min-open? true, :max-open? true} #push.type.definitions.interval.Interval{:min 22/9, :max 205.85546875, :min-open? true, :max-open? false} :scalar-sine 513484282 :code-drop #push.type.definitions.complex.Complex{:re 255522379, :im 8.4140625} 170/151} :char-tagstack 379.62109375 :scalars-yankdup #{15.0078125 [] :string-conj-set false [\¬ \Ó \< \¹ \k \e \n] :char-as-set "Ñn9ÀÓDac4°" 58/5} #{[4639 1783 1179] [] ["3IåÊtä7ÞØ»sß-¸ÆQ$7" "¨Ør" "G@i?lmÍ"] :intervals-dup ";an±gÁ1Âu" :chars-generalize :exec-when :vector-refilter}} "¦s<" \g \, ["  º%" "±Þw" "'¬,[XÄYÖ4zÚf" "Yj"] :tagspace-notequal? [2581 4777 4815 1592 2659 492 842]} "P[y)lã" [true true false false true false true false false] :string-flush [] :char-digit? :string>? [1957 1865 2016 72 3956 3253 1039 2041] "azÙ?Ø·" :string-occurrencesofchar :input!3 :boolean-swap #push.type.definitions.complex.Complex{:re 33698579, :im 334.328125} (:snapshot-dup :intervals-nth :boolean-dup :char-storestack #{:input!2 #push.type.definitions.complex.Complex{:re 355310278, :im 94.05859375} \Ã 524040576 :interval-tagstack ([] :scalars-rest #push.type.definitions.complex.Complex{:re 174488145, :im 207.4140625} :vector-set :tagspace-yank) :char-yankdup #push.type.definitions.interval.Interval{:min 179/4, :max 310.11328125, :min-open? true, :max-open? false}}) :tagspace-normalize \µ #push.type.definitions.complex.Complex{:re 646892221, :im 372.70703125} (#{:scalar-cutstack 453336256 3/38 :interval-tag :boolean-cutstack :tagspace-flipstack #push.type.definitions.complex.Complex{:re 771822942, :im 372.328125} [482 2555 2387]} #push.type.definitions.complex.Complex{:re 208955255, :im 27.05859375} [] 163/6 :strings-vsplit) :complexes-liftstack :scalars-build true [false false false true true false false] :chars-liftstack "K;´EMçÇ\"¾záP?SyP" :string-in-set? :error-stackdepth "XSäÄ~ 0×v~+/d" true :vector-contains? \s "R`=Òã+mÙ±eI+t" :chars-occurrencesof "Ì`Q9¾" :char-intoset #{:ref-clear 959918298 :interval-liftstack :boolean-againlater [] :scalars-cutflip #push.type.definitions.complex.Complex{:re 355179016, :im 48.6640625} :input!8} [\" \Ö \ \Æ \Ø \Ë \Ý \D] 240.03125 :generator-again \C #{["¡; B?¼k" "áÎ·®¾â½k'¦º¹ÌØ?s" "Ôu¤£^`ÆJÙ^f¬" "~lâ/" "¾xÉ¢" "ÞBÞ`=]sIm}´¢" "Á¬*§o" "N³ÃÃ" "A¹¾Ý"] ([\ \t] :char-return :interval-savestack :scalars-cutflip "y½®>}¡<ÆaÝo>$/Q") :chars-rest ["#~âZ<C" "ÃCe£1Ù" "fiD-Àoâ" "¦d" "´¬>;å rÞyÆ¤¯Å´RÉÞ" "¶¡mO]D(#u5ÊX" "â?)à¦#¼ç"] [3081 3793 739 3053 1104 2673] true [true false false false false true true true] :strings-print} [\ä \2 \D \Ü \»] :refs-build 26/9 :interval-stackdepth :chars-vremove :generator-empty? [4737 1933 4827 2846 2749 1239] :interval-return :scalars-rotate :refs-cyclevector :boolean-tagstack [false] #push.type.definitions.complex.Complex{:re 28547606, :im 201.953125} 55/86 :ref-in-set? :complexes-pop :complexes-emptyitem? :snapshot-new [\Ñ \h \\] :scalar-cutflip :chars-dup [0.13671875 0.125 0.0078125 0.11328125 0.11328125 0.12890625 0.05859375 0.02734375 0.15234375] :complex-zero :intervals-dup :generator-savestack true 52488797 650857653M :boolean-later [] false :complex-pop :ref-tag 719444338 :refs-empty? :refs-sampler 326.6953125 [\´ \ \5 \l \æ] #{182.23828125 #push.type.definitions.complex.Complex{:re 824838963, :im 135.43359375} true [1027 2269] :vector->set :scalar-yank :tagspace->code [1214 4042 4684 2121 1947]} :scalars-emptyitem? :string-yankdup :intervals-do*each :booleans-nth 717018889 #{276.7421875 :input!2 :ref-store [] :boolean-rerunall :generator-rotate :code-storestack} :input!2 :scalar-inc 286883906M [\) \/ \« \Ü \å \] \¡ \Ò \a] :input!7 true]



    :bindings 

    '{:input!1 (:complexes-build), :input!2 (#push.type.definitions.interval.Interval{:min 10/19, :max 22.88671875, :min-open? true, :max-open? true}), :input!3 (:error-empty?), :input!4 (:exec->set), :input!5 ("eSÇÑ¾#M}"), :input!6 ([0.07421875 0.03125 0.01171875 0.03515625 0.0234375 0.13671875]), :input!7 (#push.type.definitions.complex.Complex{:re 794890713, :im 171.125}), :input!8 ([4639 1182 3874 1975 1875 2701 4055 3491]), :input!9 (:chars-empty?)}


}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

