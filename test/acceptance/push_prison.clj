(ns acceptance.push-prison
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.types.core :as types]
            [push.util.stack-manipulation :as u]
            [clojure.string :as s])
  (:use midje.sweet)
  (:use [push.interpreter.core])
  (:use [push.interpreter.templates.one-with-everything])
  (:use demo.examples.plane-geometry.definitions)

  )


(defn overloaded-interpreter
  [prisoner]
  (-> (make-everything-interpreter :config {:step-limit 20000}
                                   :bindings (:bindings prisoner)
                                   :program (:program prisoner))
      (register-types [precise-circle precise-line precise-point])
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
                            "\n items on :exec " (count (u/get-stack s :exec))
                            "\n items on :generator " (count (u/get-stack s :generator))
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
    :program  [
      #{\$
      [4583 1557 2337 3923 3334 3216 446]
      :set-savestack 
      :set-cutstack 
      '(:booleans-concat :chars-byexample :refs-print :integer-store :circle-nested?)
      ["§i" "zÀEi/b^" "0K­uÉ" "^ÁISkk"]
      '(false #{:integer-multiply :string-occurrencesofchar :integers-set [\Ä \M \1 \®] :code-do*times :log-stackdepth false 968965594} #{:integer-sign "¦ÀË&" :code-againlater \j :line-cutflip :integers-reverse [\V \i] (:line-intersect? :boolean-echo :float-ln [2771 1622 979] true)} :set-comprehension ([0.01953125 0.09765625 0.12109375 0.02734375 0.125] :booleans-last 293.9921875 781196427 (:string-equal? [] :strings-pop :float-later :boolean-flipstack)))
      '([true false false] :booleans-emptyitem? :chars-stackdepth [1670 2863 811 1721 1980 2937 3949 4074 3979] [\¾ \µ \Ð \e  \m \¥])
      }
      :string-echoall
      :tagspace-new
      :booleans-portion
      [0.1484375 0.09765625 0.0625]
      :point-stackdepth
      :floats-rotate
      :strings->code
      :integer->float
      []
      :char-dup
      :booleans-echo
      :integer-echoall
      \@
      :set-cycler
      :char-savestack
      [true false true true false true false]
      :ref-swap
      :floats-savestack
      :char-flipstack
      :code-notequal?
      :code-liftstack
      :string-echoall
      :ref-cutflip
      :line-later
      [true]
      true
      337.3203125
      '(:float-arccosine [0.06640625 0.11328125 0.1328125 0.0703125] true #{217.6171875 [] :boolean-swap \K :chars-swap #{:booleans->code [true false false true] :set-dup [4608] :tagspace-echoall :input!4 :string-yank ["=QAÒ7" "|*/Ñ]2£" "QIGLÎK" ":tJm·½aÆ" "¶{ÓU" "b" "&3F|­¾!&([º´±teÂ"]} 70912956 :tagspace-return-pop} 86.1875)
      [true false false false]
      :string-store
      :floats-butlast
      :circle-later
      :generator-reset
      '(false 133.625 :input!10 #{:point-storestack 173.9609375 :line->points :vector-concat ["8ª-ÁÎabµ¬oÍFÉzs1" "eÐe" "_È[Nºf" "F»2Ð?<*u­m" "ºR#~KÇoº" ">Fn5P" "ÏP¢f)ÓnfM4¡2zÍ®"] :vector-sampler (:floats-echoall :strings-replace :exec-comprehension :circle-storestack :exec-comprehension) :chars-contains?} :circle-tangent?)
      :input!8
      :char-echoall
      :log-stackdepth
      []
      "XC"
      :floats-storestack
      '([3568 186 3747 1426] false [\space \¿ \Æ] :line-intersect? :booleans-stackdepth)
      :booleans-length
      :vector-echo
      :integer-notequal?
      :float-liftstack
      :integers-build
      :integer-few
      ["£-ÈÌE|ZN" "¾#&hµiW}#(~" "BÏ"]
      :input!4
      :string-storestack
      :strings-cutstack
      :chars-reverse
      [0.07421875 0.12109375 0.015625 0.125 0.0859375 0.12109375]
      :float->asciichar
      :exec-noop
      [false false]
      '(["f¯Q8"] :point-cutstack "EÔ¢5D¶K5Ge!b" :strings-rerunall :integers-comprehension)
      :exec-save
      true
      :float-π
      :tagspace-lookupintegers
      :code-notequal?
      :code-if
      :push-counter
      [0.09375 0.078125 0.0546875 0.04296875 0.12890625]
      :generator-save
      :booleans-portion
      72984301
      :chars-conj
      :refs-print
      :refs-return
      :input!1
      [0.0625 0.078125 0.10546875 0.06640625 0.1484375]
      false
      600895337
      false
      :chars-pop
      :exec-pop
      '(:code-empty? [false false false true true true] :input!4 :exec-return-pop :code-append)
      ["EË" " q6Ó" "²k¢©," "(ÄÕÂWIGLV%/®rl" "qvDÉdtRm=S³6P"]
      298.265625
      ["¿" "¯lu Å} T"]
      '(true :floats-yank "kHSJUµa1f" :refs-echo :integer-flush)
      :input!8
      "_fR¾5Ä^O6¬)"
      :ref-flush
      :booleans-storestack
      :refs-set
      #{46.796875 :refs-stackdepth :generator-savestack "RRz5(ÒÒQs*ÄF)°b" [2263 4020 2897] "^b!gAyº¥!J" :integers-dup :integers-flush}
      false
      :ref-cutstack
      :exec-s
      :chars-againlater
      :input!1
      false
      :float-store
      :refs-echo
      :circle-store
      [\Q \È \É \ª \· ]
      :string-replace
      [true]
      :ref-flush
      ["Vev5" "±'?cH%±;Y«_g" "?<vvy|z1&" "Ð¬¦h%°n=j$^ÑsÒ"]
      \~
      [true true false true]
      [\ \L]
      :string-occurrencesofchar
      [0.03125 0.125 0.09765625 0.04296875 0.0234375 0.015625 0.1328125 0.11328125]
      :input!1
      "¶NYTi{®»"
      :integers-butlast
      :integers-empty?
      "Ï&:¢6¿[/®¶f!Ãa"
      :strings-butlast
      [\ \À \Õ \o \ \R]
      '("¸y" false :integer-echoall :set-tagwithinteger #{:float->boolean (:refs-take :chars-shove :string-solid? :line-equal? :chars-echo) :refs-tagwithinteger true :strings-yank [false true false false true true false false] :chars-shatter :point-save})
      384.69140625
      :floats-againlater
      :strings-occurrencesof
      :exec-s
      302.1171875
      '(:refs-notequal? [307 3657 2041] ["T³" "0³_Æeq" "S²dÇReI¬,·" "ÆÍs~³u³¾¿»"] true :code-drop)
      #{67.1484375 :float->boolean :float-dup :char-pop :code-cons :code-savestack :chars-shatter :chars-nth}
      :input!7
      '(#{:exec-do*count :string-flush :boolean-liftstack :tagspace-cutflip :float-store true ("$9#±r=Y~" :point-equal? :tagspace-liftstack ["nÊb^w" "ÁElÅsm¸"] (false :float-subtract "8" :circle-cutstack "ºs«VW¶_Ç")) (:floats-notequal? [0.125 0.09375 0.05078125 0.00390625 0.08203125 0.0546875 0.0703125 0.05859375] [\Q \ \½ \Y] [] :integers-build)} :float-dec :refs-shatter :tagspace->code :input!9)
      :refs-contains?
      ["¤LªÏAÁ" "É¥¾" "¾<="]
      :generator-yankdup
      "1PC"
      [0.02734375 0.015625]
      :string-max
      "r]$pSsÒÈ¿²j"
      [0.13671875]
      ["ÍuP4Ge'¼" "_k¨SJaAS" "Êtbªd" "&7" "KyFN&¾©33ÌNÓ©UÈ_"]
      :refs-notequal?
      :char-rotate
      305.13671875
      :float-pop
      [4204 845]
      317.359375
      []
      :boolean-pop
      :string->chars
      [0.1015625 0.02734375 0.078125 0.01953125 0.02734375 0.11328125]
      "V°7·ª)7ÑEIÌ6©©>"
      [0.05078125 0.10546875 0.09765625 0.05859375 0.125 0.02734375 0.09765625 0.078125 0.0390625]
      :code-list
      :input!4
      [\Ç \ \P \r \ \W \L \ \.]
      :boolean-againlater
      []
      '(:floats-tagwithfloat false true ["Æ­ÊµD]}4h}lU¤ov"] :tagspace-swap)
      [true true true]
      :circle-pop
      :strings-yankdup
      :chars-yank
      :floats-last
      false
      :point-shove
      :generator-swap
      [1503 3111 137 3857 1450 2230 4309]
      :refs-return
      :tagspace-shove
      :chars-concat
      :booleans-flush
      :vector-replacefirst
      \j
      :boolean-flush
      342.3359375
      #{:environment-new "Ê'J" :integer->string true :ref-rerunall false [false false] :booleans-tagwithinteger}
      :vector-echoall
      :code-store
      :generator-totalisticint3
      '((:strings-conj :code-yankdup "ZWIpJB¾-" \m :line-storestack) [false true false true true false false true false] 307.1796875 [4032 3293] [879 4021 678 982])
      :chars-swap
      :integer-lots
      :generator-shove
      :generator-stepper
      :char≤?
      [\ \Ì \ \Ð \® \Ð ]
      :char<?
      :float-store
      :float-shove
      :chars-indexof
      [3955 673 4344 2442 475 327 264 1558]
      :integer-later
      [0.0 0.12109375 0.01953125 0.0859375 0.09375 0.078125 0.0625 0.0234375]
      [0.015625]
      '([0.07421875 0.04296875 0.0 0.1015625 0.015625 0.015625 0.01171875 0.015625] :float-storestack :exec-savestack [0.03515625 0.015625 0.15234375 0.0078125] :strings-rerunall)
      :generator-flipstack
      [" ;&@#7KVI" "nk½Æ " "¤®NS>?__¸P"]
      :ref-forget
      :integers-portion
      :generator-dup
      370016329
      :strings-empty?]
  :bindings {:input!2 [\1 \k \b \$ \S \$], :input!9 :exec-do*range, :input!3 :integers-yank, :input!10 :char-cutflip, :input!1 65.05859375, :input!8 [1472 0 3034], :input!4 :set-echoall, :input!7 :vector-portion, :input!5 :float-tagwithinteger, :input!6 :tagspace-flush}
  }
  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

