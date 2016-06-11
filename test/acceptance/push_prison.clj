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
                            "\n items on  :tagspace " (u/get-stack s :tagspace)
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

    '[["5%ÓZSÈÔÈ" "Ï¦§m!]Î»2!" "n$³N/¨­q"] 44.1328125 ([3752 4168 4086 427] [2199 3919 1944 4105 1699] (\¤ true :float-arccosine :integers-dup 35226371) :tagspace-flush :input!2) :tagspace-tagwithfloat :booleans-return-pop :set-later :char-echoall :strings-replace :boolean-rotate [3353 4609 765 558 1591 3980 652 1074 355] :exec-yank :generator-savestack :vector-remove :boolean->code [] :float-againlater [\d \M \| \S \È] :string-cycler [0.0 0.0078125 0.0234375 0.02734375] 296.328125 :floats-stackdepth :vector-portion [] #{[false true true] \d [\V \¦ \f \4 \A \u \» \$] :integers-stackdepth :char-later :input!8 :string-take :vector-sampler} false :vector-concat :tagspace->code :refs-pop :generator-rerunall :string-stackdepth [false false true true true] :strings-concat :tagspace-tagwithfloat :set-subset? :integer-againlater 673147797 :char-storestack [] [0.13671875 0.05078125 0.03125 0.109375 0.14453125] \e 556582841 ["WQ¦ÐB" "w*LÒ" "¢^o" "1º1AÐNg" "Lt>.?©¶_¥ÆYpC¤Â£" "P " "%¤}R½Ñ"] :refs-rest \E 441182363 #{:code-return-pop [\; \I] [3205 233] :vector-cycler :char-later [0.01171875 0.08984375 0.01171875 0.10546875 0.046875 0.08984375 0.1328125 0.0 0.01171875] ["ÃGi8XIOx°" "TÏÑU§¨$<UQ" "#6´q" "­µF$" "" "£§L[k¶±5»" "nªct{a¿qÅ" "¾"] (:exec-rotate :float>? :string-substring 827002023 :float-subtract)} :integers-emptyitem? :vector-shove (["¨2!" "wÓ8na" "hÌ°1¹º" "¡a?«nHxÇ«Q){" "£²Èw}"] :float-divide " #\\:¿Ï" true ["ÊÆ$Ït0[k*" "\\iC" "ZÑ2Á" "C¢+L¹hf´iÊ" "1¨N4Ç" "À"]) :booleans-cutstack :code-nth (140.51953125 "[jTi a[" :input!2 :environment-end #{160.6875 :ref-flipstack [0.1015625 0.046875 0.140625 0.0703125 0.07421875] [false true true false true] :integer-tagwithfloat :set-swap :floats-storestack :integers-do*each}) [0.07421875 0.0859375] [0.109375 0.09765625] :floats-swap :strings-echo :float-mod :tagspace-againlater :input!8 :floats->tagspaceint :vector-emptyitem? [false true false] false :vector-last :refs->tagspaceint :chars-cutflip :string-savestack [391] :integer-store :refs-shove :exec-equal? :floats-echoall :string-save :floats-liftstack [2856 637 566 4347 531 3273] [] :integer-equal? false \d :integers-occurrencesof [4968 1929 4249 1728 296 3770] :boolean-tagwithfloat :refs-remove :booleans->tagspaceint 308.52734375 :integers-save ["*k\"¿@~X`Ã" "'¯|dÊ?{MÈ[#Í" "$1" "h" "- ¯=Ë­"] :floats-new :input!3 [3031 285 2667 4800] ["7{»Y"] :string-setchar :integers-swap :ref-rotate :input!7 :ref-later ["FÆ_" "^ A¨ÎÔ¨Ç]Å#§~Ð´" "Òj%Äa<¦s" "C \"VÍ_¥"] (:refs-return-pop [\º \Q \¢] [0.13671875 0.0078125 0.0703125 0.03125 0.078125 0.109375 0.08984375 0.01953125] \Â true) :string-empty? :string-nth ":1¬=C¾%`o" [false false true] :integer>? :integers-tagwithfloat :chars-generalize [3576 3593 3299 632 2555 1705 494 659] :booleans-rotate 103911722 (:ref-lookup :refs-replace :set-stackdepth :char≥? :ref-flush) :generator-cutstack :chars-occurrencesof :vector-cycler :input!4 [true] 595341598 (:string-pop :ref-tagwithinteger :strings-conj [true true true false false true] :vector-againlater) :vector-echo :vector->set :ref-empty? :ref-return-pop :char-liftstack :booleans-savestack :chars-cutflip :tagspace-merge :chars-generalize \© 125.48046875 :chars-rerunall (["9ÔÒ`" "¡" "9" "²F}²!Åa= a§v[n0" "g8" "Ï" "._-x¸U=6Ã" "²È7g~±Z@r"] 375364434 :chars-liftstack :booleans-last :generator-echo) :ref-fullquote [false true false false false false false false true] :float-cutflip :booleans-notequal? 4425949 :refs-butlast #{:boolean-or :floats-indexof :chars->code ["ÎÁ(w\"]Å" "8}¼Ì&u£Ì4q$n" ")¼¤µÑc²=" "L3e8<Éµd%)´ " "£d±ÓvÉ"] :floats-empty? #{:refs-tagwithfloat [0.15234375 0.12890625 0.11328125 0.1015625 0.0 0.0546875 0.01171875 0.08203125 0.1484375] :integers-swap [false false true false true true false] 373.05859375 :strings-last :print-newline :code-comprehension} :code-position :floats-cutstack} :string-later [true true false true true false false false] :vector-liftstack :vector-return :float-echoall 214.8984375 :generator-return-pop :input!2 [false true false true true] [0.07421875 0.140625 0.109375 0.06640625 0.12890625 0.1015625 0.03515625 0.0625] 801780170 :string≥? [] ["3Êfu(z3Âf:"] "mI<ËÊbu/½µ5ly" [0.109375 0.1484375 0.046875 0.03125 0.08984375 0.109375] 340.21484375 :tagspace-lookupvector :integer->char true 691129711 false [] :set-print :booleans-notequal? :boolean->string 3.38671875 :strings-do*each [\v \¼ \J ] [] :tagspace-equal? :boolean-liftstack :char-tagwithinteger :integers-pop [false false false true] true :ref-exchange :input!6 [853 4016 3850 2134 157 90] 176727458 :refs-build (:string->chars :exec->string :string-spacey? 34.89453125 :exec-savestack) :input!9 :push-instructionset :integer-dec :input!1 :strings-reverse :generator-liftstack :strings-new "J·C,·" :integers-dup :string-rotate [0.140625 0.14453125 0.06640625 0.02734375 0.0] :string-comprehension [0.04296875 0.08984375 0.0546875 0.02734375 0.02734375 0.08984375 0.109375 0.14453125] "|\"Ò³·*Z8E@thY" :floats-notequal? :input!6 :set-echoall :floats-length :generator-return-pop :float-notequal? :tagspace-tagwithfloat (:floats-byexample ["¶È" "¾t{ÎNÊ]O~3,ÄY" "$Àr ^Ò\"6:¹" "=@'Ïxk¿" "~Õ3´2ukyo"] [\Q] "¸ImF\\´r«\"Â©" :float-dec)]

    :bindings 

    '{:input!2 (:integers-pop), :input!9 ([]), :input!3 (:boolean-print), :input!10 (:char->code), :input!1 (true), :input!8 (\&), :input!4 (:integer-print), :input!7 ([false true false true]), :input!5 (:code-tagwithinteger), :input!6 (true)}
}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

