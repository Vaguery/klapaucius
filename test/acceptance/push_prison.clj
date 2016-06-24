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
  (-> (make-everything-interpreter :config {:step-limit 20000 :lenient true}
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
                            "\n items on :scalar " (u/get-stack s :scalar)
                            "\n items on :complex " (u/get-stack s :complex)
                            "\n items on :snapshot " (count (u/get-stack s :snapshot))
                            "\n points on :tagspace "
                              (fix/count-collection-points (u/get-stack s :tagspace))
                            "\n points on :snapshot "
                              (fix/count-collection-points (u/get-stack s :snapshot))
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

    '[("Âá[Ç$.!B\"oU$g" :input!5 (:chars-do*each :scalars-build :scalar≥? :scalars-yankdup :tagspace-shove) :input!1 :complex-scale) :chars-rotate (:scalar-lots #{23.47265625 :input!9 \å (:generator-yankdup :char->integer 79/146 false :scalar-cutflip) :complex-reciprocal :tagspace-sampler \Z :vector-echo} false :vector-indexof :scalars-remove) :vector-refilter true :char-liftstack :scalar-complexify ["ÎÜ\"" "]²¼_ÄÁÉ6nvI¾oÈ7" "1XL¾Td9i" "Jh>Þ¿E2+/"] :boolean->signedfloat [0.01171875 0.02734375 0.09375 0.12109375 0.0703125 0.12109375] :strings-later :strings-generalizeall :string-shove :tagspace-storestack :code-cutstack "4Z" [\» \5 \9 \= \Õ \ \w] [\y \À \¢] [0.15234375 0.12890625 0.0234375 0.1328125 0.04296875 0.1484375] 60/107 :code-null? :vector-concat #{:exec-flush (\ false :generator-cutflip 39827270 [0.1484375 0.12109375 0.0625 0.0]) (:scalars-emptyitem? [4564 952 2095 4601 4239 275] :booleans-cutflip :refs-remove (:char-flush :tagspace-keys :chars-indexof false 564746474)) :chars-rotate #{:set-stackdepth :complex-subtract :booleans-remove true ["©®ÚOt6" "<8²¢ É8" "Ù'RÍç@{æ´¿$J8§" "e­ª" "Í&°f·]mp8Ö¡W^5/"] :tagspace-rotate :scalarsign->boolean [false true true true]} ("Ç[£BÄ!Jß©Ó,ÅdwRÛ" \Ó :complex-subtract #{:booleans-equal? :complexes-return-pop true ([0.109375 0.05859375 0.0625 0.0078125 0.109375] 614765428M :code-echoall :booleans->code ["¼^ÎÁ¢×,RÒµãÜ" "`Ð^´¶/y¯¼Ï" "¡VÏ7IiÕPy¡±5²I©¥ÐT" "æ" "" "ÍX"]) :strings-length :chars-reverse :scalar-arcsine :refs-return-pop} :ref-swap) :chars-storestack :chars-save} :booleans-remove [0.1484375] :booleans-yank 254.60546875 true :complexes-do*each :char≤? :tagspace-flipstack [879 3413 1442 4418 764] [true true false true false true] [0.140625 0.05078125 0.09765625 0.03125 0.0234375 0.01953125 0.05078125 0.05078125] :scalars-emptyitem? [3987] "Í»xÚ*+{¼çR" [1590] 629851952M :strings-empty? :scalars-print [false true false false true false false] :tagspace-echo 850927731 :string≤? 197881115M :set-later :generator-yankdup :vector-refilter :char-later #{196.04296875 [\k \< \ \Å] [] [0.1171875 0.00390625 0.0234375] :tagspace-comprehension false [0.13671875 0.078125 0.06640625 0.0859375 0.078125 0.05078125 0.0546875] :char>?} :scalars-dup :code-insert 961569546M 236.40625 :tagspace-storestack :strings-build 8.54296875 :exec-dup :generator-totalistic3 [0.02734375 0.109375 0.14453125 0.10546875 0.1171875 0.12109375] :refs-cutflip \Ö :boolean-arity2 (["?³Z?©D" "0±`¤ÞÎÆ¼:Â2" "IÎ;Î4*c¯'L¥y£" "I³ÖJ!ÈC¥)æ" "v_Ú@Ù" "¾7¶«zhmB$6QDd-Z" "'x!*| Ër" "«á"] [\~ \e \ª \Ï \Â \d \% \I] :tagspace-max :chars-nth ((:scalar-min :boolean-return-pop :exec-do*count [0.13671875 0.1328125 0.12109375 0.08203125 0.13671875] 394939191M) 141.74609375 66291492 #{374936468 "®yçÍMÛ\"~n²wHMÕ.PXr" :chars-byexample :strings->code ["gCßÃ·" "Ìh¹Ga2-ËÁ"] :generator-empty? :exec-comprehension [\m \Î \µ \£ \¶ \r]} :scalar-min)) :scalar-divide (:char->code [")$QÄ?ª" "ãHnKnTr¼" "VBIÜ{=Ô#8,J%pEvÇ" "¾t:¾Â" "M|Ê" "NÞÝ¹" "1[È¤Û³$q×®KAQ8" ";¨æJOÕ)\"K¿Î«¸" ":á·Ø"] :chars-print :ref-tag :tagspace-savestack) :input!5 [" /" "!ÀKD»¤Æb¬±²®Õ=" "¯Ï" "ØPÃX[1cfbs4(-ÜÖ" "àR¿7ÑG" "¿¦P.*JØ/Î" "ÌÅ¿6]" ":±MUÄÃäØR·" "vÙ¶?\\ÙIÌZ×»"] :vector-tag :input!9 :input!7 :boolean-echoall :input!10 102177971 :exec-if 309.96875 :vector-store :tagspace-keys :char-rotate "Å5NàÔ1$4«Ö" :input!3 :booleans-cycler :vector-reverse :print-empty? :scalar-multiply 4/17 (:chars-savestack :complexes-take [\¿ \e \O \ \Ù \O \ \C \%] [true false false false false false] :set-sampler) [3267 345 2710 4707 2485 1976 2511 2461] 156912706M :scalars-occurrencesof [3145 1247 599] :input!8 [0.11328125 0.0 0.03515625 0.0 0.08984375 0.09375 0.15234375 0.0625 0.0625] #{"VÄWK" :tagspace-tidy [0.02734375 0.00390625 0.06640625 0.125] :tagspace-rerunall :scalars-do*each :exec-store \Ø :string-yank} :snapshot-notequal? :chars-new :exec-store 193.02734375 :tagspace-yank :booleans-rerunall :vector-echo :scalar-many [true true true false false false true false] \Á :generator-pop :chars-new :strings-swap 10604759 [false] false true :exec-sampler 587007939 :char-shove 684227885 :strings-do*each :complexes-storestack :ref-yankdup 159/37 166.6171875 ["­àJ¨Y@Ïå¿xªP" "%L!.|µ HÓÒå°" "?^Te#«¬ªagMà" "ÜNÜ¥æ" "\\Û4Á¯R"] :refs-portion ["e.@p<&"] [2415 756 3768 4749 4044 2314 2875 1276 2480] :input!5 :char-flipstack :ref-print :ref-flush :vector-rotate 839861686 [926 2631 1138 924] (:exec->string [\@ \ \^ \Ê \s \? \L] :scalars-take #{["c&7z2Ã2OT" "¥eoÌÚg7°aÅÓr" "S`" "GÍJx" "wÜÌÇÁ¼" "3\"ÎS^xPw ³¶w" "4¥gPØb" "C5ÐXÝÄ" "½dUsioFD¢FR¹.v_ÍÞ"] :complex-stackdepth :chars-set [2576 2826 773 2003] :refs-flipstack :chars-shatter [2400 4970 2227 3615 2547 2] "æ°,2­H×L·å¹ÍZÚ"} :scalars-indexof) :strings-againlater [false false false] [\ \Í \¨ \Î \h] 117/196 :booleans-concat :refs-flipstack :exec-string-iterate :snapshot-begin :scalar-rerunall [\à \P \ß \B \j \± \Ê \I \D] :strings-yankdup :complexes-conj :generator-stackdepth :string-cycler #{["Öj" "t±/" "Á" "ºÔ" "CS¶Ð!F¿@'¹i²"] [false true true true true false false true] [930] [true true false true false] 875649387M :complex-empty? \{ :exec-do*range} [1019 1904 1549 3482 1507 472 3082] 5 :boolean-dup :generator-dup :code-sampler :string-againlater [true] [true false true] :tagspace-tidy :char<? :push-refcycler :ref-liftstack true :ref-yankdup :exec-swap :input!3 :booleans->tagspace \* ["ÐÇ±" "r@¶ÔY.¹k0¤É" "7j;B\\" "5J" "ªB½aYÄ\\)u" "à×Û/N"] [false false true true true] [false] :chars-conj :push-bindings :print-empty? :ref-lookup :code-extract :boolean->code :complex-return :snapshot-tag :input!5 :vector-rotate 199/2 :chars-liftstack :strings-replace \ :set-stackdepth :refs-generalizeall false true 957135328 :code-quote 155/132 :input!9 "RÍ>v6" :string-tag :exec-equal? 305752160M :exec-do*times 28/181 :boolean->float :string-occurrencesofchar :code-do*count]



    :bindings 

    '{:input!2 (:tagspace-new), :input!9 ([\Ò \· \º \k \e \°]), :input!3 (:complexes-flipstack), :input!10 (:vector-comprehension), :input!1 (:string-reverse), :input!8 (:scalar-dup), :input!4 (:chars-reverse), :input!7 (19/42), :input!5 (:complex-tag), :input!6 (:code-notequal?)}


}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

