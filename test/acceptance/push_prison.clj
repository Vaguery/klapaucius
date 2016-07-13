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

    '[:intervals-butlast ([] :scalars-remove :input!9 :scalar-round [0.14453125 0.00390625 0.0390625 0.1171875 0.05859375 0.08203125 0.01171875 0.015625]) #{(96.890625 264.2265625 853731762M 229.73828125 #push.type.definitions.interval.Interval{:min 24/115, :max 218.7890625, :min-open? false, :max-open? false}) true :generator-flush 113846428 :log-stackdepth [0.0078125 0.04296875 0.0703125 0.1015625 0.10546875 0.04296875] \Ú [0.078125 0.0703125 0.1484375]} :exec-cutflip 381099787 :booleans-generalizeall ["%JgÝ¨rrÔÍW" "br8SÓ®_ÆNws'3_ç." "" "~J´Ý" "$±ºVºÑF" "yáoH'æ/Ï«O­|¥"] [\L \e \£ \@ \i] "Z1¤_" :float-uniform :set-empty? [1210 2035] \ :boolean-echoall :exec->set 284.48046875 :scalar-bigdec? :error-empty? :vector-remove :scalarsign->boolean :strings-conj-set #push.type.definitions.complex.Complex{:re 533725802, :im 252.08984375} :input!7 (#push.type.definitions.interval.Interval{:min 21/22, :max 202.23046875, :min-open? false, :max-open? false} :intervals-byexample #push.type.definitions.interval.Interval{:min 167/127, :max 257.5859375, :min-open? false, :max-open? false} :input!5 \) :interval-echo false 341.58984375 \¡ :interval-save [\¥] :set-tagstack :input!1 :ref-liftstack 279.08203125 :boolean-pop 212142800 :booleans-as-set "eÐBådQ0©,Vw[" :vector-return [false false true true false true false true] :scalar-print [0.0078125 0.0703125 0.13671875 0.14453125 0.08203125 0.13671875 0.0390625] ["xÐJ" "Å=xÈMÙfÔ5g]v;È" "SA:%+mVÆ­OÝ27X·" "»µã¤Ó9Q¸e'¬JÜË3" "h4<f°8Ö" "}tàvg" "¾°"] :boolean-rotate [0.12890625 0.11328125 0.10546875 0.12890625 0.00390625 0.1484375 0.078125] :snapshot-shove :code-cons \8 :intervals-vfilter 92815578 [false true false true true false true] :input!6 :booleans-remove :tagspace-storestack :vector-emptyitem? [] 142/119 [false false true true false true true true] :complexes-print 455553115M [] :string-store :boolean-tag :input!2 (\ [true true true] [] :booleans-vsplit :char-tagstack) :vector-build (:snapshot-againlater (:scalar-reciprocal #{#{239.33203125 ["Í\\¨·&Åid0½$1" "ÜÛ@SdE$¥"] :scalar-many true :ref-later :chars-remove \4 [\Y \V \s]} :strings-build ["m''\\u:;KB" "n¾ÔÐN¥!]Ò¯f^Ý#" "Ì?§çO6Ï3I" ";" "6@«Ó¯ÕPÅj¯®" "¥áCnJçh"] :tagspace-keyvector :scalar-tagstack #push.type.definitions.interval.Interval{:min 1/9, :max 319.87890625, :min-open? false, :max-open? true} 17/10 #push.type.definitions.interval.Interval{:min 14/107, :max 32.84765625, :min-open? false, :max-open? true}} "³»ªÈÀ³>¤ª-Õ]kæÑ" :strings-vsplit :refs-flush) #push.type.definitions.interval.Interval{:min 113/193, :max 212.78125, :min-open? false, :max-open? false} :scalars-conj-set #push.type.definitions.complex.Complex{:re 980560452, :im 100.68359375}) 884765707 :strings-stackdepth :ref-echoall false "¤æ¼ry}ÂM" :input!3 :input!9 false ["jÄ" "P¾S?NâËÝ" "á'XÙT.Y_ª2ÄÞ" "ªØå3Z®`Û>2L" "®p¨w¦«iÎ7Ï"] :refs-dup :complexes-swap 943980989M :set-liftstack :exec-save false :scalar->asciichar :interval-rotate false 224161058M "ËNHº¤Y¼" :snapshot-cutflip "±(ÏÛSr" :boolean-xor [\ \ \ç \p \w \­] #push.type.definitions.complex.Complex{:re 54616818, :im 306.51171875} 6.0859375 :ref-cutflip \§ #push.type.definitions.complex.Complex{:re 943283418, :im 231.0234375} :vector-storestack :refs-distinct [4843 4254 4995 3218 4891 2668 1026 4359] "¹ <¡#Â´SHtu¦Ö6\\l ©" :refs-new :chars-shatter :refs-vfilter :interval-save #push.type.definitions.complex.Complex{:re 808230142, :im 368.171875} 61592146 [2419 4923 1645 2589] "OÌÅÁ" :scalar->string :booleans-rest :scalar-yank (:intervals-portion :strings-return-pop :intervals-portion :intervals-tag :scalar-liftstack) :string-dup :code-sampler "N68¦µ+g5¸¼" 817184772 :tagspace-keyvector :booleans-savestack :exec-laterloop :chars-equal? :input!6 #push.type.definitions.interval.Interval{:min 51/76, :max 223.37109375, :min-open? false, :max-open? true} :tagspace-echoall 895362571 :string-shove [\b \® \Ä \: \V \ß \+ \' \I] [false false true true false true false true false] [0.08203125 0.015625 0.15234375 0.0078125] 158/29 "ÆÝ)72" :integer-uniform true :interval-pop 99652082 \\ [\Í \¶ \Y] :intervals-length :vector-conj #push.type.definitions.interval.Interval{:min 71/82, :max 136.328125, :min-open? true, :max-open? true} :strings-rerunall :ref-return :tagspace-comprehension :char-in-set? #{561361533M 233749378 :generator-liftstack :scalars-conj ["y­R" "Ï>LyÁ¹k@äºâgnBáXN"] [1225 2852 607 4163 1492 2296 3578 3580] :exec-s [0.00390625 0.015625 0.1328125 0.078125 0.1328125]} 169180273M [true true true true true] :scalars-print :set-difference [3648 3921 1179 4915 3959 4150 2225 3277] 372.7734375 "Ú<ÎECq_ç·Ò$Í" :tagspace-count #push.type.definitions.interval.Interval{:min 49/30, :max 161.234375, :min-open? true, :max-open? true} ["ÏIC\\" "¶ã\"DÝÚäÊ+`«¥"] ["E*Q²°mØç«±" "A`Ëä1­Ï$UÍ{³© @×" " 6©uÏÞ£*CC" "¹1Q ©)®hkÉ^" "¼ 8Þ"] 210267556M 70/89 :snapshot-flipstack #push.type.definitions.complex.Complex{:re 308801832, :im 126.421875} #{:interval-print [0.078125 0.078125] 63368334M :chars-conj-set [true true true false] false 422136777M :booleans-cutflip} :booleans-cutstack :input!6 [0.0 0.12109375 0.0625 0.0625 0.03125 0.00390625 0.0625 0.078125 0.03125] :string-echo 66/161 "ä¿åWQÑÔxbã]ÄS" :booleans-in-set? "" 493271875 [0.12109375 0.14453125] :complex-scale 792715492 false :vector-rotate #push.type.definitions.interval.Interval{:min 71/139, :max 369.4921875, :min-open? false, :max-open? true} :complex-yank :generator-tag :refs-vsplit [2438 939 2230 1469 2742 3096 4176 272] :chars-emptyitem? (:char-dup :complexes-butlast (#{212.12890625 440592827 :scalar-some [false true true false false] :scalars-swap false :refs-sampler :set-echoall} :scalars-sampler :push-nthref \¿ :code-echoall) 979931308 #push.type.definitions.complex.Complex{:re 130766958, :im 384.38671875}) [\° \ \Ý \D \D \T \: \å] :input!4 \( :complex-pop 183.07421875 #{#{[0.0859375 0.08984375 0.0234375 0.13671875 0.046875 0.0390625 0.04296875 0.10546875 0.0234375] :input!9 :scalar-integer? [true] 75100897 false (:chars-rest \D :ref-pop (:input!8 [4072] #{:interval-swap :interval-intoset false ([0.109375 0.0546875 0.1015625 0.0703125] [949 2739 3249 3759 666 1236 4172] :chars-yankdup [\{ \| \» \ \R \+ \\ \] 860170347) #{:booleans-build :string-savestack #push.type.definitions.complex.Complex{:re 40161026, :im 72.6484375} [] "º" :input!4 \7 :scalar-float?} :code-rerunall :set-later #push.type.definitions.interval.Interval{:min 2/3, :max 337.34765625, :min-open? true, :max-open? false}} (\c :interval-later 780034692M 801902129M :boolean-rotate) :strings-vfilter) :ref-rerunall) [2986 742 1401 478 4142 883 3406]} ([0.02734375 0.01171875 0.13671875 0.09375 0.0234375 0.08203125 0.12890625] true 87.74609375 :interval-subset? :booleans-set) true 14/29 "rQsyXÁO¨J#" ([true false true true false true false] :tagspace-remove 351.0078125 2403733 ["rÐ+[F&Æ8Ñº»Ö" "¨n*¶q§+z^ßÀ" "ÉAÂÌÀDà" "¾B²^Ç_ÊkXÊ4ÛK" "v" "âb4" "ÝÒ(* ¢Þ"]) :intervals-store #{:char-lowercase? ["ÒÝ|EÚN¨J»X¯$oKC" "ä~Ñ|¿¢}CQH¦±" "×wzÚ" "3f)qf×|¦5"] :intervals-rotate [] :snapshot-flush 380163119 #push.type.definitions.interval.Interval{:min 177/2, :max 142.484375, :min-open? false, :max-open? true} :input!1}} :code-member? :string<? :exec-swap #push.type.definitions.complex.Complex{:re 823757693, :im 182.8203125} :vector-shatter :code-echoall :scalar-save :char-swap true :complexes-in-set? [\ \â] :tagspace-cutoff :vector-nth]



    :bindings 

    '{:input!2 (117/79), :input!9 (:set-echoall), :input!3 (:booleans-first), :input!10 (:scalars-return-pop), :input!1 (:snapshot-notequal?), :input!8 (340.65234375), :input!4 (:string-return-pop), :input!7 (:booleans-save), :input!5 (:scalars-contains?), :input!6 (#{[0.05859375 0.11328125 0.0625 0.1484375] :intervals-pop "Ð·yc{¥Ù å" :scalar-as-set :char≥? false :string-containschar? #push.type.definitions.interval.Interval{:min 67/7, :max 166.875, :min-open? false, :max-open? true}})}


}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

