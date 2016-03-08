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
              (println (str (pr-str (u/peek-at-stack s :log))
                            ; "\n >> int: " (pr-str (u/get-stack s :integer))
                            ; " >> " (pr-str (map count (vals (:stacks s))))
                            ; " >> " (u/peek-at-stack s :booleans)
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
    {:program [:integer-max \౎ [true false false] :point-flipstack true ["ߺ୾" "࢈̀༝ᅮߏᆲdͻ൵Ǆͮዔીᇵԡ" "ࠐኜऍฝෛM዗࣢׳" "Ų"] :point-storestack :code-cons :string-shove ["႔न܆฾ຮ߱ణቤ୒ൕःढຜጄ" "ࢿ৑෬Ⴎ©੎џዔ༢ຟࢃਆ࡬" "ቨ஘ᅗࢸ၎ೠု" "פೊѦ౮ङʴ" "ᄃ׬ग࠳ޢ༽ౢࡓळ੻" "ģႝ" "ฯ౵֭Ɍ̅ʖཀྵŮોਫ਼" "ෑԹا϶Ѱᆵܒࢼٗࡨውೲვ÷ᆠKK٬ᅟ"] :circle-equal? :float-arccosine 257160786 ["ঘѠጋࡸقึֵဥ"] [0.06640625 0.00390625 0.13671875 0.01171875 0.12890625 0.0703125] :string-setchar :input!7 [false false] \ഝ \ᅀ :float-cutstack [0.0625 0.1171875 0.05078125 0.02734375 0.00390625 0.0859375 0.05859375 0.125] :chars-emptyitem? false '(:set-store :floats-conj :integer-sign #{:point-yankdup :ref-empty? [true true true] \ሑ "ᇈჵ༨๑~ဓ๳৯" :code-member? :strings-flush :chars-nth} :circle-cutflip) :floats-last '(:refs-flush :integer-flipstack :string-comprehension [0.01953125 0.03515625 0.15234375 0.08984375 0.12890625 0.12890625 0.0234375 0.15234375] \ɜ) :floats-dup :float-multiply "ศਚਏȹ࣓܅ۀƈͤძኂՆংಯᄽٸ" [false false true] :code-position 212.0078125 [] true [\௕] :refs-rest false :strings-occurrencesof :integers-occurrencesof :exec-cutflip :booleans-yank :exec-if [2565 1362 45] :ref-lookup 27.49609375 214.7578125 \ȴ :floats-cutflip :vector-rest :vector-savestack :input!5 :line-flipstack :string->float :point-return :exec-dup [false true] :floats-do*each false :chars-yank :integer-equal? :integers-do*each \૸ :char-max true :integers-notequal? :boolean-againlater 64.671875 :integer-cutflip :generator-flush :booleans-portion :string-butlast [\ݸ \࿌] "ುލπ׶ཹ࿧ࢯڗ୹þ" ["ʹัࠍᅈጀԏሣࡘഋя৻጗ڐڝ໼৓" "о୊CƢࡏࡅȁ֮كকሕ੆υഐব" "ಏ¨ǯΞࢿ჎ེ፹ॽǍेޣ" "සࠆକΰū༡ǫ߻ղ዗ଠ੸՝ዚϏɄ" "%ް\tࣟʙŰȰ୛ê໮ş஠࿎ྞऑൽ৶ीɐ" "Đࣱڷ࠷" "ྡே٪႖৸ߩৈǵŷ੸ஊ"] [2867 4575 483 2441 3634 3749 3271 2572 2708] #{129.5625 [0.0625 0.04296875 0.01171875 0.04296875] :booleans-pop :set-intersection :booleans-emptyitem? :floats-set ["ዀ໊ყЉƨٰêʦྏ࣡ۦ࿹࿂ſե+" "ɔมφᆻ" "ࠤࢽฌࠒಥཌྷºੰၳႠऑЍ፺ᅀฺל̸ோֵԯ" "ӎ್ጟЪʊϜඞ૳फ़ݦ୏֭࠙Ԑͦଡ଼Ǎஒಁຸ" "σѽ޷࡚ᅩົಆ೚" "ٕ੢ጊ" "ಽ՞ٗമ஘՚ྂ" "ࠓؾ಍ᇾङරØ"] [\֢ \፥ \ؚ \ഽ \࿮ \ˮ]} :chars-flush :floats-emptyitem? :vector-print \୆ [] false "ܛXް҄ȍ႟༁૮°øಒÆኬ׍൚࿍࣌" "႑ॹ˫ਆ" :strings-shove :integer->string :exec-comprehension 721822386 :floatsign->boolean #{["ƳႸጔ်შ໖൱ၓၱƶ୏Ήᅮο፭ǿ૜" "૑ચ໼࠺཰ቱඑë" "Ƞ׺ฆؚၪǮࣱ঎ڽ̝Ƅږ؎ۏѮ" "͝ቄܳᄢ٪ࣲ๶੠኏ሩऺܞఞ໨" "ጩѪኟൎణࠛ" "ჩऑ࣑" "ᄚ͌ঐ༑౼ƮɸയӨഭஅে൫Λžၭ௷ஊ૓ሲ" "ǰဌிৢւึǃᅎè" "ิුϋথஃ"] :set-notequal? [4393 1115 3921 516] \ࢫ :string->code ["፶ҵങ~ॡ࿏ɿ͋" "༣ᅾ༕ඈĆ਍؊ཊൺ੝νߏম฀" "ժ્ႜ࢖ዌҮ๰ሉȚኺܡ૝૧"] :input!7 :exec-do*times} :ref-storestack :ref-rotate :floats-pop 52121096 :string-substring #{:refs-conj :integer-save :floats-last :code-map :char-stackdepth :integers-empty? :vector-set :code-member?} [726 2662] :integer->boolean 356.12109375 :set-intersection :integers-contains? :exec-notequal? :string-return-pop :code-wrap ["gႴϜڡᇏཱߡݺࠋ\tழயͬʩ?܂ጌ" "άۍƱࠤֶ఩ݮċżኝ϶ఄȸॗݯט̼" "ț̞ݒаï׌੺1෭ૅᄉɑƈ֐ଢ႐ࠨባ"] :boolean->code :chars-emptyitem? :string-dup \෢ :strings-return :refs-remove :exec-comprehension :boolean-swap :strings-flipstack :refs-cutstack 35483782 :float<? "ԗϋ຤ˬѽᄤ૪ó" :line-shove :refs-dup [\ტ \ܘ] [\ሾ \ݟ \͋] :line-dup ["་¼ߨ཈Õ༼್ܝȸऔ"] :booleans-contains? :chars-flush :string-save :string-empty? true :circle<-points :char-return-pop :float-ln1p :integer-swap false :floats-new [0.10546875 0.0625 0.0859375 0.01953125 0.1015625 0.00390625 0.11328125 0.03515625] true :strings-return-pop :vector-stackdepth :strings-comprehension :point-storestack :chars-emptyitem? :char-pop "එԓ͙ਲ਼Ҋ஥భး˒ఆݑՒಖߍළऄօࢂ" :line-parallel? #{:string>? [0.03515625 0.03515625 0.00390625] [false true false true false false false] :refs-rotate :integer-cutflip :chars-shatter :strings-flush :push-instructionset} false \޻ :string-take :chars-flush :exec-flipstack :booleans-portion \శ :input!7 :integers-replacefirst [] :exec-while '((:code-againlater [\޷ \৆ \཯ \४ \Ϙ \ኹ \ై \ฎ] [0.0703125 0.09765625 0.05859375 0.08984375 0.1015625] :booleans-new :boolean-xor) :refs-take :string-later \ڱ "ѺȢ") [] true [] :vector-take [\b \ፄ \࿴ \ੜ \લ] [\ݚ \௄ \቙] :ref-echo :line-savestack :floats-return-pop :code-notequal? :float-yank false 198.84765625 :char-shove [1665 1188] :integer-dec :exec-noop :floats-shatter :set-empty? :string<? \ຮ "ᅔ൘්ܙԐஙοɏჍ" [3916 1609] [\ۿ \ௐ \൪ \Ð \è \Ǡ \࢟] #{:circle-shove [] :floats-flipstack :integer-cutflip [1194 1382 3808 4536] :point-swap :chars-empty? :boolean->integer} :floats-flush :boolean-swap :vector-refilter :line-cutstack [] '(:generator-storestack :integers-rotate :boolean-or :char->integer :input!3) :integer≤? '(true :integers-notequal? :refs-concat :floats-echo :input!7) :code-cycler :vector-yankdup :strings-return false :input!5 :generator-next [4407 3661 3830 1756 396 847 867 2724 252] 70.87890625],
  :bindings {:input!1 :float-E, :input!2 :set-superset?, :input!3 :vector-byexample, :input!4 [0.078125 0.02734375 0.1171875 0.15234375], :input!5 :refs-cycler, :input!6 [], :input!7 :set-notequal?, :input!8 :refs-emptyitem?, :input!9 [0.13671875 0.0078125 0.11328125]}

  }
])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

