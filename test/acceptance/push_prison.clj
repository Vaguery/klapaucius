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
              (println (str 
                            ; "\n >> generator: " (pr-str (u/get-stack s :generator))
                            "\n items on :exec " (u/get-stack s :exec)
                            "\n items on :unknown " (u/get-stack s :unknown)
                            ; "\n items on :chars " (u/get-stack s :chars)
                            ; "\n items on :string " (u/get-stack s :string)
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
  
  {
    :program 

    '[142.01953125 :tagspace-tidywithfloats :set-print :vector-emptyitem? :string-pop :strings-tagwithfloat :ref-print :chars-comprehension :booleans-occurrencesof true [] (["_9 NGk" "5Q_£Î:w\\¤MË" "R~ÖÆsÆniaÎ" "«5mÁÇ(¹Ü¤Û¯´Í ±" "¦á¾OQÓ.É0@9" "µ" "$W^Ô{s" "x{E"] #{:integers-later :strings-equal? :vector-againlater :tagspace-tagwithinteger 435448840 :vector-emptyitem? [\] [0.04296875 0.09375 0.10546875 0.09765625 0.02734375 0.02734375 0.08203125 0.1328125]} :strings-concat #{211.8203125 :push-refcycler :booleans-equal? :refs-nth :refs-flipstack "²åÃOHbçª&$¡F" 684402898 :float-flush} :chars-stackdepth) :chars-contains? [0.04296875 0.140625 0.109375] :set->tagspaceint :floats-concat :float-store :chars-store :ref-print ["y*#56'yÑbÙQsdKÙ{ÜX" "[³ªt!W>»Sa0»k¥Í{´Õp" "j" "ç" ";.^Ê.çÁ"] (:generator-return :ref-stackdepth :code-tagwithfloat :ref-shove :ref-return) 311130013 [true true false false true false true true false] :set-empty? :booleans-first :input!9 :integers-occurrencesof (413211116 :strings-comprehension [\å \8 \i] "-ä¸DÔ±æá)>" :push-refcycler) :char-shove :integers-cutflip :vector-store :input!1 :generator-echo :code-yankdup :refs->tagspacefloat true [\D \| \d \v] :refs-pop [false false false true] :print-stackdepth :input!10 :char-pop :set-cycler :tagspace-comprehension :char-letter? [0.04296875 0.1171875] :float-savestack 455714759 :chars-byexample :integer-some :generator-cutflip (:refs-return :exec-yank (:refs-pop (:strings-take :code-size :input!5 157.1171875 :input!6) :integers-generalize :vector-savestack [0.1015625 0.03125 0.09765625 0.1328125 0.140625 0.1015625 0.0 0.140625]) :integers-rest []) :strings->tagspaceint :integers-cutstack 83.2265625 :booleans-occurrencesof :string-comprehension [0.015625 0.08984375 0.0 0.11328125] 261.32421875 39.05859375 :floats-flipstack :input!3 :exec-liftstack "S·Ò\\ÅÏjs+Ï½uØ" (:exec-return :tagspace-stackdepth [false false true true true false] :vector-tagwithfloat :string-storestack) :input!3 :booleans-againlater :boolean->integer [false true false false true false true] [1828 4125 1433 3910 4630 2035 2952 2989] :strings-equal? (:tagspace-flipstack [\b \4 \@ \¦ \! \L \¤ \o \á] :set-echo :input!7 [0.03125 0.00390625 0.0703125 0.08203125 0.0390625 0.08203125 0.05859375 0.09375 0.125]) :floats-remove [false false false false true] 962283694 :floats-return-pop "?4\"b¿J4Ü/" :vector-cycler 296.59375 [\ \È \ \o \©] [2557 2407 2700] :integer-yankdup :integer-stackdepth [\ \. \] #{[\º \H \É \space \ \F \; \<] [0.02734375 0.125 0.05078125] :boolean-cutstack [\º] :chars-storestack false :ref-return-pop :booleans-replace} false [\ \ª \0 \] :string-take :vector-rerunall :refs-byexample :ref-liftstack [0.14453125 0.01171875 0.00390625 0.04296875 0.01953125] :float-power :floats-stackdepth ["ÇÝÓCr"] :integers-last :integer-save :booleans-portion true :char-uppercase? [0.03515625 0.04296875 0.03515625 0.11328125 0.0234375] :strings-comprehension :float->boolean 375047931 :strings-concat :integer-digits [\" \0 \1 \n \¹ \@ \] (187.203125 399791282 328.41796875 :char-uppercase? :char-yank) :integers-later 168.62109375 "×åÐGÕ74SvÓF/¿eÇ" \} :code-echo :integer-notequal? [\¡ \M \c \» \, \] :boolean-save :code-do :chars->tagspacefloat ["§E" "¦rk@­x"] :floats->tagspacefloat :refs-rest :tagspace-yankdup #{:refs-comprehension #{:float-yankdup [0.0390625 0.0703125 0.0703125 0.12109375] (:boolean-savestack :ref-echoall 388.7578125 [2372 3630 4652 4097 2255 3367 2136] :integer-biggest) false :refs-pop :boolean-return :strings-shove :tagspace-echo} :vector-contains? \g :refs-notequal? 73673344 \· :exec-notequal?} [0.1171875 0.00390625 0.03125 0.015625 0.08984375 0.06640625 0.13671875 0.0859375 0.125] :char-tagwithinteger true :strings-build #{:input!9 [false false true] :exec->string :float>? :strings-againlater [3206 4942 1657 3550 3149 4028 99 444] :booleans-tagwithinteger :ref-lookup} :integer-uniform :float-arctangent 471901348 :char-echoall :input!1 :chars-flipstack :char-swap :tagspace-scaleint :integers-replace :integer-return :float-uniform :integer-divide :strings-stackdepth :chars-nth :generator-return :float-subtract :refs-liftstack [true false false] #{[\Ï \c \ \J \{ \% \H \3] [4642 1176 1443 4481 3910 1316 2342 3712 4676] :generator-jump :strings-echoall 244294257 "næR,ÜZu¡=" :float->integer :booleans-cutflip} :float-rerunall :booleans->tagspacefloat \ 381722219 "k6z]§qtÙ" (:string-substring 199.421875 :integer-inc ["¬`6j§Õa~[R"] true) :input!7 #{:float-yank :input!10 405150295 :string-indexofchar :input!1 [0.10546875 0.10546875 0.140625 0.01953125 0.13671875] :refs-echo :set-sampler} :exec-liftstack :integers-shove :generator-echoall :boolean-or :strings-flipstack :generator-cutstack "¨L" :floats-indexof :string-reverse #{:code-flipstack [false true] :booleans-equal? 554934418 :strings-nth :boolean-savestack :integers-againlater :code->string} [0.08984375 0.1171875 0.125 0.03515625 0.03125 0.0859375] true :input!3 (:input!5 "6ÈD?+oAm" :generator-tagwithfloat ["+åk«Òr8-ÞH#H#" "j~|/¿k0" "·F K\\â"] [0.0625 0.09765625 0.0703125 0.078125 0.01953125 0.109375 0.04296875 0.06640625]) [] :refs-stackdepth :integer-storestack :ref-yankdup 368.640625 [false false true false true false] :generator-empty? "Ýx~}Ô'¾vh)BU¯f" [] :vector-byexample :code-subst #{\ :boolean-yank [] :boolean->signedfloat :input!1 798680050 :float->integer :refs->tagspacefloat} #{90.09375 [3394 4757 871 3769 3105] :floats-replacefirst :vector-againlater :intsign->boolean [true true false true false] :tagspace-sampler :string-yank} :booleans-build :boolean-empty? :ref-tagwithfloat :generator-liftstack :ref-flipstack :chars-replace :vector-first :char-empty? :refs-take 204.1015625 :chars-stackdepth [\½ \æ \á \Á \¡ \/ \] :exec-liftstack :vector-take :string-flipstack 253.1875 :string-max "S\\%¥¤,i¤*cÎL"]



    :bindings 

    '{:input!2 (749297146), :input!9 (:code->string), :input!3 (:string-removechar), :input!10 (:generator-yank), :input!1 (#{:refs->code :strings-dup :chars-length :floats->code [false true true true true] #{:set-flipstack :boolean-storestack [] :booleans->tagspaceint [0.08203125 0.04296875 0.00390625 0.01953125 0.04296875 0.05859375 0.05859375] :exec-noop 616337908} 153958758 :exec-when}), :input!8 (:floats-later), :input!4 (:boolean->code), :input!7 ([4954 321 1435 1639]), :input!5 (:string->chars), :input!6 (:generator-later)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

