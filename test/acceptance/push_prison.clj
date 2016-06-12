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
                            "\n items on :booleans " (u/get-stack s :booleans)
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

    '[:float-log10 :boolean-faircoin :ref-known? [\× \Q \® \( \Ò \Ö \C \$ \S] :integer-echo ["¦m?°âJ*B[!c" "ÐÚYqQçml" "Ú²FKbGÉ¬\")ä!" "|A" "¢h~" "0Î¹b^>Ô¨$ZOÊkã" "¡+¬Z¦7A±n´«" "(R9q(©¦à" "mUGÙ"] :floats-concat "$!aeq:®Ë¬âÃ}" true #{:input!2 :strings-nth :floats-build :boolean-shove :exec-string-iterate #{:refs-length :strings-dup [\& \Ç \  \] \Í :chars-return-pop [651 127 296] :set-save :generator-next} :strings-last :input!5} #{:integers-yankdup :code-quote :char-stackdepth (:vector-tagwithfloat :strings-shove \E :float-arctangent :floats-new) :floats-pop :boolean->signedint :integer-bits :tagspace-return-pop} :string-cutstack [false true false] :string-butlast :input!2 :code-cutflip 704857863 :booleans-flipstack :float-min true :strings-new (200494064 :float-min :string-storestack "¥«+cµPÂ[" #{:tagspace-tidywithfloats :input!3 :booleans->tagspaceint :exec-tagwithfloat :float-liftstack :tagspace-lookupfloat :refs-liftstack :input!7}) :strings-echo :exec-cutflip :refs-reverse :floats-tagwithfloat ">¤ÁNZ²Pv" :strings-store [\f \I \Þ \)] 353.08984375 :ref-fullquote :tagspace-lookupintegers [0.0703125 0.09375 0.12109375 0.1328125 0.15234375 0.03515625 0.12109375 0.02734375 0.13671875] :float-echoall :string-tagwithinteger ("×ÉÊ" :float-equal? :floats-occurrencesof :vector-echo :ref-tagwithfloat) :booleans-notequal? [false true false false true true false] :environment-begin :refs-pop :integer-abs :generator-yankdup [] (:floats-save :generator-savestack false \Ö :ref-save) :integer-echoall :refs-nth :booleans-yankdup :chars-dup :string-return 511534523 :code-contains? \Ê :input!8 18.421875 :exec-cutstack :integers-rerunall :code-length [\ \À] :set-cutflip :tagspace-sampler :input!6 :booleans-shatter :refs-replace \± :error-empty? :print-space :booleans-new #{:code-size :exec-againlater [true] :refs-replace :booleans-savestack :string->code :boolean-echoall :string-contains?} [] :refs-dup :floats-remove :error-empty? ["¢vQ%Ûpwåntæ&k?" " ÏÖ¤Ë4" "gpN¿Ë5)Óã"] #{:vector-savestack "X¢ºPÐÇ¾¶L" [] :exec-string-iterate "bXß g ÆtoJ(%ªÕçÏ" [\ \] #{[1370] :code-print :chars-return-pop :integers-shove [false false true true true true false true false] :code-shove :input!7 :float-rotate} :floats-return-pop} :integers-nth :tagspace-return-pop 461393681 [\° \ \K \µ \space \% \F \V] :chars-pop false :float->code :integers-butlast :vector-shatter :input!2 355776800 :integers-pop :vector-echoall :booleans-shatter [\± \T \C \ \ \å \Y] :strings-indexof :float->string 201.734375 :integers-storestack :refs-concat [true true true true false] :strings-build [] :ref-pop :code-container :floats-stackdepth \ä (:floats-length :code-cutstack #{:environment-stackdepth :input!2 :string-print :code-length 196358019 :booleans-nth :string-emptystring? :booleans-save} :integer-uniform :integers-replace) :code-container :integers-indexof [true true true false false true] :vector-indexof :tagspace-pop :floats-cutflip :generator-counter :integer-add ["µ¾³ºN-½"] :input!8 :boolean->float :code-notequal? [\¬] "x»Hy9#" "gH?Õ:4ß" :floats-save [false true true] :integer-stackdepth [3380 1158 3270 4289] true :refs-indexof :code-insert [0.13671875] :integers-generalizeall [0.00390625 0.00390625 0.125 0.01953125] :integer<? :code-pop :code-equal? :integer-dup :booleans-cycler :string->tagspacefloat :string-shove :integers-build 319.60546875 :refs-shove :boolean-store 37.26953125 :integers-rest :vector->set :input!3 :floats-savestack [\ \o \ \»] :strings-byexample "·°U88" :char-lowercase? \6 :float<? :chars-replace true :code->string :refs-flipstack :chars-generalize (:tagspace-sampler :chars-tagwithinteger :char-print [] \v) false \C :float-E :strings-concat ([0.07421875 0.015625 0.140625 0.01953125 0.046875 0.1328125 0.078125] :set-shove [] :vector-storestack "!XXzÍl5") [] 538492791 :integers-build :boolean-againlater :chars-print 716087766 :booleans-generalize :refs-cycler \p false [\n \] \ \Ë] false :integers-take true #{:integers-shatter :chars-length :exec-if true :integers-emptyitem? (:code-cutflip :push-bindingset :chars-shatter "Å¡b7~u¬YÄGÉ7c" :strings-first) :string-reverse :floats-return-pop} :strings-print :string-dup :chars-conj 78.5703125 [3296 327 3074 823 420] 40173048 :vector-new :integers-yankdup :floats->tagspaceint :floatsign->boolean false #{#{[\Z \¡ \ç \ \ \ \!] :float-yankdup :generator-save :vector-cycler true :input!8 :tagspace-lookupfloat :strings-flush} :vector-print :exec->string :strings-set true :generator-reset :ref-known? :tagspace-return-pop} :string-splitonspaces #{\K :vector-stackdepth (true :set-return-pop #{:input!2 :integers-pop \1 \Ö :set->tagspaceint [\N] [0.1171875 0.140625 0.09765625 0.1015625 0.1171875 0.0078125 0.1484375] :floats-return-pop} :integer>? :strings->code) true 339.18359375 :float->code :float-flush :input!6} :tagspace-min :float-sine 319935115 456765026 :exec-cutstack :chars->code :refs-cycler \x [0.09765625] [4047 1846 2318 781 4023 2430 4211 1183] ["¤áb2I?" "bËf:" ":cÖÏØkh£" "?i'Tä_´Tç·'V'" "]«2Ã7c|3Cp°Û³" "]ÅL{" "(hQÓW`¿£Tä"]]

    :bindings 

    '{:input!1 (:refs-rerunall), :input!2 (#{160.51953125 :code-map :refs-savestack :set-shove [true false true true false false true false true] [0.06640625 0.12109375 0.140625 0.01171875 0.10546875 0.00390625] :vector-comprehension 458569580}), :input!3 (:char-print), :input!4 (:generator-flipstack), :input!5 ([3974 2279 1390 3185]), :input!6 (174.7421875), :input!7 ((:booleans-yank [\Z \È \K] #{:integer-uniform :booleans-cycler :tagspace-storestack [\) \i \} \v] :code-member? :booleans-concat :set-difference :chars-stackdepth} :vector-return)), :input!8 (:integers-set)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

