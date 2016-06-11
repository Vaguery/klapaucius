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
                            "\n items on :strings " (u/get-stack s :strings)
                            "\n items on :floats " (u/get-stack s :floats)
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

    '[(["ÞmÞÊ" "\"" "½hÜ2]i)eÃM£" "´DãÓÐä]pma×O¤" "" "V$$§FËÂbx" "pàÎ%Nß¥R" "{'dfÍÃ<·I" "»Ï±ICªV¦69 ¥"] :strings->tagspaceint :floats-flipstack "®¯°Á°´zk" :code-rerunall) :tagspace-echoall :integer-echoall #{:set-flipstack :strings-cutstack :float-pop [0.08203125 0.0703125 0.12890625 0.1015625 0.0625] :float-shove :booleans-empty? :tagspace-lookupfloats :set->tagspaceint} :strings-comprehension :exec-pop #{#{:strings-notequal? 175693345 :floats-butlast :booleans-new :strings-remove :environment-empty? :refs-echo :char-flipstack} :integers-save :floats-replace [\K \v \Ó \Â \m] [3824 2739 4413 1620 243] (87.4921875 :chars-pop :boolean-echoall ("ÄSxE+¼f%Ìw2ÒLµ[2fÁ" :booleans-cycler :floats-store [4162 2950 699] :input!4) :exec-empty?) :floats-nth \t} :input!7 :vector-shatter [\È \` \· \^ \4 \9 \Õ] [0.0859375] :string-rest :vector-emptyitem? :code-flipstack :integer->code [\­ \ª \¾ \Ã \" \¼] :tagspace-scaleint [212 751] :set-rerunall :refs->code :strings-flipstack :vector-yankdup :refs-cycler \£ false :boolean-againlater :code-cycler :integers-storestack :float-sine :string-pop [] :vector-storestack :strings-replacefirst \Â "ÂÖÃÆMG¼ÞµÑ" :integer-savestack [25 4340 2147 2016 528 2717 3660] 605563706 \ :refs-comprehension [] [" ¢8 ¡BXEÚrÄb¸§"] 259684236 :float-notequal? :chars-yank \g :vector-length :chars-save :refs-new :refs-savestack :integers-generalizeall :string-occurrencesofchar :input!9 [true] false "àj~MX" :booleans-notequal? 524487697 :generator-stackdepth :float-dup [638 4296 3756 1605 4399 4622 919 4346] :booleans-liftstack :floats-echoall :integers-swap :chars-shove :booleans-emptyitem? "z" :chars-length [false] 162.6015625 :ref-lookup :generator-reset false ["B3`\\Æª×qÛ]/mÜ"] :refs-length ["®" "GÝ"] 242.3203125 :refs-byexample :string-nth :input!9 :char-echoall :input!5 (:float-ln ["W\"=Z²Ê5Îw~ÂVP" "aÅÐ¿XÚ³Ê&@=D@(" " ZW ¸=gÆÎ>³ÆÒ7~Û~" "ÑIrÎz$PÈ!\"h?" "È3d]}ãOKO·8àoÝ" "6¼¼F±4ÖphÎReLR" "H#JàdN|8Z%aV" "}µ"] :strings-new :integers-replacefirst ["­à!¦Ã%" ":±+Âæ" "{¢j" "¤A0¥Y¥­¨"]) 550074735 :input!6 :chars-return :generator-again :vector-yank 479550906 :integers-remove :float-arccosine :strings-emptyitem? false "jËd.³J½#XÚ@Ýå" :tagspace-sampler :boolean-stackdepth ["[¨á$70¾ÃU¨" "=»" "3N9A¼rÅä¢½8f ©{" "UË?l¬â" "I"] :exec-k "_mhÈ pähWw\"E<¬o«m" :integers-swap :integer-many 108159340 [\¿ \Æ \U \À] :floats-concat (:exec-flipstack #{:tagspace-splitwithint :exec-echo :set-yankdup false [\ \s \ \£] :exec-s :float-rotate :char>?} true [] []) :strings-butlast :tagspace-stackdepth :booleans-length :booleans-savestack \Á :integer-mod :tagspace-sampler :chars-store :strings->tagspaceint [653 3792 4808 1340 1674 462 4428 456] :tagspace-lookupfloats [] :vector-cutstack :code-rest :tagspace-flipstack [] :integers-cutstack :string-equal? :ref-save :refs-cycler 982476717 :boolean->float "äqÛ¶ÝC@e#G`Ì.z;s" :input!8 :generator-flush ">ÞÃ~F" :chars-build \B ["y5" "?[n¡ZWj¦8d" "¾E!ÛÂ" "8a¬" "<rNBÜi|æ#EC8Ûá©%Õ«Ã" "8´f0kdM¥³+dT3á|ç" "Å¾n£" "×{Õ©ÁV%¥#1µØ?N"] 910426726 [0.1171875 0.00390625 0.046875 0.01171875 0.125 0.0546875 0.12890625 0.1015625] :input!5 ["pv¡\"%]¨DUÓ#" "¡%±y·{l"] :floats-return :vector-new :char-print :float-multiply [2301 2612] \d #{:floats-length :exec-savestack [] :booleans-return :floats-equal? 90628938 :ref-stackdepth} 566825723 :chars-butlast ["ÝAsæ¬k(C" "UNIgb×­À(}" "7°-'®l=)ÎA'b" "9=Y" "_" "¾¤`coO®u"] :boolean-swap :boolean->string :string-pop :string-pop true :floats-last 148.6484375 :refs-reverse :float-cutstack :char->float 983946242 :boolean-return-pop :integer-smallest [\¯ \] [\® \C \u] :float-flush :tagspace-cycler :refs-generalize [2646 2458 913 4774] :string-spacey? [] [\© \e \ \M \ \ \] [true true true] :ref-exchange :ref-storestack :code-first :strings-later :tagspace-tidywithints :char-yankdup :boolean-againlater :integers-reverse :code-cutflip :tagspace-cycler :string-return-pop [false] :integer-smallest :integers-return :input!8 :boolean-shove [\h \ \Ü \> \Ë \| \] 361.1484375 false :tagspace-dup \µ :float-shove [\A \Ú \ \e \ \space \,] ["§á¶mE]Ô×" "ÏC.Áç/PØà#ÇÂÕ-D"] :float-swap :integer-tagwithfloat (:integer-bits 327.21875 :boolean-liftstack :tagspace-stackdepth (#{[0.14453125 0.01171875 0.12109375 0.0546875 0.13671875 0.125 0.140625] :vector-cycler true :char-store :ref-yankdup :float-min (:refs-cutflip :booleans-flipstack :exec-when :refs-emptyitem? :float-yank) #{[3253 2174 2049 1956 349 3782 2929] :exec-flipstack :code-list (:vector-notequal? :exec-later (#{["=6Ê[×uH-7bZA" "Åç*z£" "¯ºm"] #{:exec-echoall "Âi;/ÕDÔ9<ºf" :booleans-savestack \² :tagspace-pop [0.15234375 0.0078125 0.015625 0.0859375 0.05859375] :tagspace-tagwithfloat [\¤ \£ \ \' \å]} \Ã :boolean-2bittable [4387 4799 929 1885 4009 1650 237 1355 259] :booleans-concat :strings->tagspacefloat :float-flush} :set-store :vector-yankdup :refs-set :integers-concat) :booleans-new "JOÅà<­Ì]") 705884567 :integers-portion :integer-echo :integer->bits}} :ref-cutflip false :booleans-tagwithinteger :input!7)) :code-rotate :refs->tagspacefloat]

    :bindings 

    '{:input!1 (50.43359375), :input!2 (#{:strings-portion :boolean-or [633 1831 1573] ["®Ædjk±O[^­2æ" "°£Í!Á9j«91t¿]"] :integers-store :chars-tagwithinteger :code-return [false false false false false false true true true]}), :input!3 (#{16.734375 :vector-first :refs-savestack [true false true true] :integers-notequal? :ref-save}), :input!4 (\w), :input!5 (:vector->set), :input!6 (:tagspace-scaleint), :input!7 (#{:tagspace-tidywithfloats :float-multiply (:booleans-concat :string->float :char-cutstack false \o) [316 1403 699 3144 4261 3189 1450] 305.39453125 ["¯¿D²À*FQ,©§ÈÉÖV"] :ref-new (:float-yankdup :chars-cycler :float-log10 :boolean-storestack)}), :input!8 ([1198 3915 2871 4232]), :input!9 (994819714)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

