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
              (println (str "\n>>> " (:counter s)
                            "\n items on :exec " (u/get-stack s :exec)
                            "\n>>> ATTEMPTING " (first (u/get-stack s :exec)) 
                            "\n items on :tagspace " (u/get-stack s :tagspace)
                            "\n items on :generator " (u/get-stack s :generator)
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

    '[:booleans-rerunall (["Ù2âÙåE[~cÕæß3GÕ" "ZÁ²tÍãq³z/ÂÍ" "<b+"] :exec-when ["Ñ" "®È" "#«(¾Æ@RÒ}x&Îy4ß" "Z!ª>ÑÔ" "`]Üã5Ã²$" "#¶Î" "(I4|UW¥O-:²ÞÝ" "Ï~¨°LxÏ*<,E»" "oÐXU´¸»9ÐkàÓ"] :scalar-add :ref->code) :vector-pop :code-quote :scalar-flush :tagspace-keys [] :scalar->boolean :string-empty? :booleans->tagspace :string-dup ["¯b?*ªà" "·]L·¾ãQ;C" "°+Í14tSf" "?Ýä4Ì$" "N®"] :char-echo :strings-new :exec-storestack #{[false true true false false false true true false] :chars-concat :input!3 [false false false false true true false false false] true :exec-sampler ["+\\º CpÁ)ª§@_{LÄ" "­]W¸uÛ" "ßÆifEA~#!^ ´j" "Hj1­x#WÅ×²º+µÞ\\" "´á19" "SiÏ¹6*hyqQ'c«±T$"] ""} :input!10 :booleans-yank :input!3 false :push-bindingcount [true false false false true true false] [true true false true false true] :tagspace-dup :input!8 :refs-return #{[0.04296875 0.109375 0.0078125 0.03515625 0.0703125 0.109375] :set-print :exec-rotate [3645 776 2826 539 880 261 1224 2273] (["Ê½P©Ù[Cgu¸B9E"] 209.64453125 42/5 :input!2 :vector-take) :code-noop :ref-flush [false true false false]} ["gÙ,H.nGØ" "=uf·9c².ãV<$c" "¤¼æ8«¨cV " "£j1" "çf^Ð¢3;?´-"] :char-pop :scalar-savestack [402 1347 551] 97/58 [0.09375 0.046875 0.15234375 0.09375 0.06640625] :refs-shatter :code-atom? :print-empty? :vector-cutstack 19/4 :input!8 :code-dup :chars-contains? :scalar-save :code-noop :vector-conj false :string-removechar [1035 2534 4811 449 4073 4754 3143] \¹ [\² \Ø \+ \© \¿ \] :vector-rerunall \ :refs-nth :tagspace-rotate :exec-stackdepth :input!2 :exec-do*count 30/77 :scalar-add [\) \Ô \U \¬ \! \:] :generator-return ("Ë1ÉÛ+*­]Ý >hÚÃÖ" [906 3490] [false] :scalar-storestack :vector-concat) [3587 2433 2983 326 4972 2797 4704 737 4870] :scalar-notequal? "m¡Z©gÉ" :strings-occurrencesof 347388147M :exec-cutflip :chars-save :input!2 :vector-take :strings-set (707213229 :string-equal? :strings-contains? :exec-do*count 117.125) :set-cutstack 28/25 :refs-stackdepth :generator-pop 42/61 :code-comprehension (:booleans-flipstack :chars-butlast [\7 \° \× \Y \J \Ñ \a \Í] :tagspace-tag :refs-take) :strings-rerunall :vector-replace [\Ö] :tagspace-comprehension (:exec-echoall [3236] [] :input!6 :set-swap) :scalars-cutflip :exec-echoall 271.80859375 :char-min :chars-set :char-dup :scalars-flipstack #{:refs-notequal? "ÔÙ¤µÜçyq" :code-map [\ \Ø \ \» \# \Ü \G] :char-rotate :string->code [111 4137 4316 2158 4985 1100] ["0yvæÝ­D ^³)Ö]wEkW" "cÊäÙVX" "A?âk<³¯`³K\"C}"]} [false true false] :code-contains? :scalar-return :strings-new [\£ \G \V \) \] :code-position :scalar-ln ([] 355.17578125 21/200 757957377M :chars-return) :refs-portion [] :exec-do*range [4882 2888 3699 2016 4240 4582] :chars-cutflip 974187429 :strings-flush :booleans-take :push-bindingset 240.44140625 \² :tagspace-min :ref-flipstack [\P \k \m \? \q \ \¶] 643001480M \Í [] :scalar-ceiling (:code-empty? :set-shove :tagspace-tidy :code-equal? :error-empty?) :strings-replace :generator-rerunall :scalars-store :ref-later 159359130 [0.09765625 0.12109375 0.1328125] 810916421M :input!5 false (:scalars-conj :set->tagspace :boolean-notequal? :vector-remove :chars-generalize) :code-length 25.6796875 [3835 2857 515 3493 4483 1674 542 4070] #{[false false false false] :vector-contains? :chars-take :string-tag :exec-equal? #{#{:strings-store :scalars-save (:environment-new :vector-yankdup "»)É²°YkJ³" :push-nthref :booleans-swap) (:code-echoall [1401 1585] :chars-do*each :string-print :code-swap) :scalars-cycler (:tagspace-yank :push-instructionset :ref-notequal? [0.0703125 0.07421875 0.03515625 0.02734375 0.11328125 0.0234375 0.0859375] :ref-yankdup) [344 4103 4262 1211 4894 1735 860 880] :scalar-sqrt} [false false true true false] :generator-flipstack 170/69 "0cNãE¥×%ÄÚÂT" [1811 1095 3651 1190 3738 4770 2608] :vector-flush ["¥DD(j/­ä" "a?" "ÃÛQ" "C´°¿QBE#6U?" "]r8â\\¾¤t`ÕNç#Øpâ¡"]} :booleans-cutstack :vector-sampler} :set-return 517802284 :generator-rotate :chars-replace :refs-generalize [] :refs-cycler #{:set-subset? :boolean-swap \M :scalars-swap false :exec-s :scalars-remove (false :string-equal? :refs-contains? [] #{:tagspace-max :string-setchar [] true 43/26 :environment-begin ["g82¿ç¾" "tpµ(}°+k" "|e4n\\Hc³Ö»­Hª­" "Ä½&?#¨Ì«M" "¼Ë38u®&á<¢¦FaÅ" "<vmC¸Ï0)µB7ä="] :generator-store})} 128.34765625 :exec-cutstack :code-later 63/58 #{219.05078125 :strings-store 170.62109375 [] (:string-flipstack 59/60 :set-cycler "lÛNªÕ!¦)Ô1" \) [false true false false true true true] :refs-set :strings-contains?} true :vector-comprehension :generator-totalistic3 :input!7 [1522 1005] [3708 2019 2411 2146 1358 4077 2755] 770072216 ["ÈºÝÊ" "ÝÚ)&Í5¦Zhk" "yÓÇÓwI»" "°0hÆBX¼xµVch" ".gÕ(^7Æ¦3" "eNKr" "ßWfj°¥MÃs¶­wrk%" "ÂÓ@\"ÑÊcw" "-kAJ&µ«s µÌ"] "'" 47/12 260751667 :code-atom? [] "°Ï½PGev`XÈÙ.I" :input!5 :scalars-againlater :scalars-notequal? :char->code :set-empty? false :strings-swap #{(:scalar-print :chars-store :set-save :generator-yankdup \Â) :scalars-rerunall :boolean-store :refs-yank :ref-flush :set-save 478395916 :scalar-arctangent} [\t \ \ \C \£ \× \>] :booleans-later :scalars-notequal? ((:set->tagspace [true false false false true false true true] #{:vector-replace :vector-dup :tagspace-later :tagspace-cutflip :chars-remove :tagspace-store :booleans-yank [0.11328125 0.05859375 0.10546875 0.05078125 0.03125]} 76798544 :boolean-rerunall) false 354379421M :booleans-storestack 274472753) :char-empty? :refs-comprehension 757180302 :input!5 :exec-pop 277.83203125 :scalars-new :input!7 (:string->chars :code-store [0.0625 0.15234375 0.0 0.11328125] :exec-s false) (:vector-butlast [false false true false false false true true false] :chars-last 815807497M :generator-yankdup) :string-first [true false true true true] :scalar->code :char-save ["LG¯/Wkl®bw" "Â" "tq)x-kpc*äT­½È¢6" "^Î).¶a" "¨×eÖ°ÁÁfÂRÅtà!)"] :environment-stackdepth false 448261890 "ß¤æ_" :boolean-rerunall (:scalar≤? 253018336 [0.08984375 0.0859375] 228.55078125 [true true true true]) :scalar-pop :chars-empty? [0.0625] [1327 4 236 3160] [true false false false true] 248.84375 39]



    :bindings 

    '{:input!2 (:boolean-return), :input!9 (:vector-notequal?), :input!3 (:set-storestack), :input!10 (:exec-notequal?), :input!1 (:ref-fullquote), :input!8 ([]), :input!4 (:boolean-save), :input!7 (:string-equal?), :input!5 (:strings-cycler), :input!6 (:scalars-portion)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

