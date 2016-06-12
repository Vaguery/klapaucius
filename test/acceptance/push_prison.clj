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
                            "\n items on :integer " (u/get-stack s :integer)
                            "\n items on :code " (u/get-stack s :code)
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

    '[#{\! :chars-swap [true] 321.76953125 :push-counter :chars-remove false :ref-yankdup} :floats-empty? [false false false] :set-echo [4901 3716] :booleans-tagwithinteger :set-echo :float-max :char≥? :booleans-conj :string-rotate \ :integers-replacefirst 331.13671875 [true true true true false false false true false] :boolean-flipstack false :chars-emptyitem? :boolean-echo :input!9 (["]£\\" "wq¶±)Ü½ä«M´"] :push-bindings :vector-sampler #{:float-subtract [1820 2987 63] :boolean-againlater 694903247 [true false] false [0.1171875 0.0390625 0.046875 0.09765625 0.14453125 0.140625 0.10546875 0.03125] :float-flush} :exec-storestack) [0.08984375 0.078125 0.11328125 0.109375 0.13671875 0.12109375 0.1171875] :string>? :code-yankdup :refs-conj [true true false true true true false] [0.14453125] :code-againlater :refs-empty? :exec-print :input!3 163.96484375 :integer->code [3805 3406 3067 4883 917] :char-save :float-storestack :strings-pop [] :refs-storestack :code-comprehension [0.0546875 0.125 0.02734375 0.05078125 0.02734375 0.12109375 0.15234375 0.13671875 0.01953125] #{:string-occurrencesofchar :set-yank [] :floats-equal? :tagspace-flipstack (:floats-notequal? (:set-cutflip [false false false false false false true false] :tagspace-offsetint [false] [0.046875 0.04296875 0.0234375 0.15234375 0.1171875]) :integer-tagwithfloat \ß "zÞ") :string-shove (:exec-empty? :tagspace-scaleint :input!9 \u :tagspace-new)} [0.0703125 0.09765625 0.03515625 0.125 0.10546875 0.01953125 0.05859375] :booleans-generalizeall (:environment-stackdepth "QM" :floats-echo [true true false true true true false true true] 777207466) 777391506 :vector-empty? ["4äÂ.%luL©ÞSÌ%" "HÊ°Íß" "n*"] ([false false false false true false true false] (:boolean-rerunall :float-π :char-stackdepth :chars-nth :integer-store) :char-later :boolean-flipstack [0.03125 0.1015625 0.109375 0.1171875 0.04296875]) true :integer->code :integers-yankdup :float->asciichar \A :booleans-cycler ["TÏ{¾ÞE¶:" "ÍoÞâÈ´¤É"] :code-print :char-pop :integers-return :integers-pop [false true true] :integers-flipstack ("7}Þd§(äBwÊÔËoÁ" :code-do*range :tagspace->code :boolean->signedint :tagspace-scalefloat) "j(CAÒ¢]Ô" :integers-nth :refs-sampler :floats-rest :integer-subtract 292.78515625 235387254 230.62890625 :input!3 true :integers-againlater ["Ý½ÃÕ" "Ýp&+" "o¾\\:a!æ=X" "¡ÞZÅÜAGÈÜç\"ÙÅ" "½&"] :integers-empty? :refs-contains? [] ["ÆÇ" "°OÏ}i+cÖ³Ôl,S#v|" "vm}³ÏYãSP=çÔ" "¿?Øµ°S6sË1" "ccEkjO8¿ÝtYg" ",£Vç*s&tG1]>Â" "pÕm2<¨å"] [\] :integer-min :integers-rotate :tagspace-lookupint [1749 4405 1066 2197] :chars-rest ["-Qf«]¯eàk" "\\l2¬wÒ×ç¦" "¶âG-D}Ýàga¨m" "PIfÁÞdÝ0Á»r" "9¦H¬Á8·6Í²·"] [false false false] [0.0078125 0.0625 0.00390625 0.0859375 0.0 0.09765625 0.05078125] :environment-begin :code-yankdup [false false false true true true false] "¸;Åz«¬0Åwh©~ª?Ím#" \` :refs-build "5rl¤\"" (:char->string :string-dup :code-size true [2291 1288 4835 2441 3810 838]) "0Õz¹Ú@ZSÞ£OVÃ{p[}" [0.078125 0.09375 0.0625 0.09765625 0.02734375 0.01953125] [\N \5 \Ë \X \Ù \Ð \4 \0 \z] #{293.8984375 [0.09765625 0.05859375 0.0390625 0.03125 0.046875] [] :char-swap ["1×ä?H©cã=ÞÕÉ©æÁ"] :booleans-concat :chars-flush :code-rest} \Y [0.0703125 0.12890625 0.1171875 0.11328125 0.04296875 0.07421875 0.0546875 0.078125] 935913545 :strings-return :integers-new :booleans-return [true true true false true true] "¤_" :exec-tagwithinteger :refs-store :integer-abs :input!2 :input!4 104.79296875 [4912 3356 998] true :ref-swap :code-return ["ÔÉd°zIª" "Àâ[¦<DRhÄ¶" "[-*0³q(pj£ã=ØÉJ¯" "p?a¶"] :strings-yankdup 110.9140625 425739486 ["q«JàÏ«ÇÞ:ÝK\\[ª" "azQ=<Á9Òh¢m¢:BA" "©*L¿! Ý!««bLæ£w" "nT" "Èã~Ý²" "Ê_ÊªÁØ,bâ·/Ñ!mj" "w Ç®$®;"] :string-min :vector-replacefirst :integer-abs :integers-contains? :integer-yankdup 301641308 [0.140625 0.05859375 0.046875 0.140625] :float-equal? :boolean-store :input!5 :tagspace-rotate :code-echo :exec-echo "¢mgRL;wD" :integer-shove :booleans-swap :tagspace-tidywithints [false false false true] :floats-shatter :generator-totalisticint3 :code-do*range 954062092 (:integers-set :float-multiply ["ã" "BÞm±ÑÜ,Þ"] 362915236 \1) :char->string :integer-max [3050 4038 4705 4238 235 2124 3099] \< :tagspace-cutflip :chars-conj :chars-byexample #{:integer-dup [] :booleans-generalizeall :floats-againlater :vector-set \ :refs->tagspacefloat \\} :tagspace-new [3516 2571 2387 1975 3191 2153 4260 3667] :strings-echo :booleans-nth :exec->string :tagspace-keys (:strings->code :integers-stackdepth ["LL^" "C" "HJz" "²Àfx" "=ÞÀÚ¶" "2Ø¯Âm:Ò" "" "ÖAmkBda&[áBG}¶:Ìàb²"] :vector-remove :vector-concat) :string->integer :chars-concat :strings-stackdepth :set-flush :integers-set :char-storestack :strings-generalize :set-yankdup 61.890625 :ref-clear 258.484375 :string->code #{:exec-tagwithinteger :input!9 :floats-cutflip [0.0390625] :floats-empty? "?@Ä¨HÉ+&²^½xÅTB¥A:" :ref-savestack [0.04296875 0.015625 0.05859375 0.046875]} :chars-pop :float-uniform :tagspace-yank :char<? :string-solid? :integer-smallest true :generator-storestack [true true true false] true [0.0625 0.05078125 0.0234375 0.12890625 0.1015625] #{["SÌÀ¸" "^y°kÅ\\}·Í#nº¤Ð)F"] :refs-equal? :environment-new [930 1843 160 235 714] true "¦" :code-container :string-first} true #{176.828125 \ :string-tagwithfloat :string≤? :input!3 :tagspace-tagwithinteger 84881286 :chars-equal?} \ :code-do*times [false true false false true] 459530860 :chars-storestack :exec-comprehension :float-arccosine [] :string-later 25.83203125 :set-yankdup \-]



    :bindings 

    '{:input!2 ([]), :input!9 (:float-min), :input!3 ("Ú ä"), :input!10 (:string-save), :input!1 (:ref-save), :input!8 (:string-rest), :input!4 (:chars-cutstack), :input!7 (:string-return), :input!5 (:boolean-empty?), :input!6 (:generator-rerunall)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

