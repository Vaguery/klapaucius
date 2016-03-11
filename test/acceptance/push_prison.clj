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
                            "\n >> generator: " (pr-str (u/get-stack s :generator))
                            "\n" (pr-str (u/peek-at-stack s :exec))
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
  ;; 
    {:program [:line-shove ["஖ኾ࿺๚৸˟ཚഴȠፙຢୁ@ᇽÝጛ஗ᄨ֜" "ʫxଦɄ൭௕ਭ" "ढ෩ኣᎁଏǢÈ˾ࠐ፲"] :vector-replacefirst #{94981432 [\ଇ] :refs-remove :float-shove :integer-echo ["ॷӔጤ" "ၫ" "स໽׈ٛሷHॠࠡڇ኶௦ೳاᆸ" "ࢬੌൠႵਂ່୘ۃɝᅳœࢹᄼছಾݚት" "ᇥ৷ެ4ႍʀඡఃހᄗᅒಞ" "ܳီྋќњ" "ڝ׻धѱঋجӈ౎.ჰഭϓඍኝ੎୎" "੝ᄋҾଌኘ૞җӳඏEऍಮ΍ொͥ፟఩"] \Û :code-reduce} #{'(\ԉ :vector-take :refs-emptyitem? \Ⴥ :boolean-cutflip) [\ē \೾ \Ϧ \ฃ \҇ \ፖ] :line-equal? '(:code-do*times :chars-replace :boolean->integer :boolean-notequal? :floats-cutstack) :exec-storestack :strings-do*each :floats-flipstack :circle-inside?} :booleans-last :float≤? :strings-later :refs-nth #{:set-flipstack \ɪ :input!1 false :code-member? :code-empty? '(:circle-yankdup 815465028 127.64453125 ["לጻඕଁ࿂ဠयީတ" "ನઃ༌" "ٔໆௌே଍ؔЂவषॣၨᄔো೗ቈŞߧ"] 3.6796875) :floats-first} [0.0078125 0.0703125] :exec-rotate true :string-echoall :line-later :boolean-return-pop :circle-tangent? \ᅠ :chars-do*each [0.1328125 0.1484375] #{:integers-replacefirst ["ಠ೚" "ୠ౓ጽରࣘǛᇾዴ" "˞Ѳͳİ±ጬúӏ̡௬ጧ࠼Йୗ಩ፘÏ" "ጪ࿺ۜक़ǌၮʺٌĹ" "ॆከ஁௟Ƈǿ೰Ⴋ࿭ೞ" "ະԈP޲ȞȂީʮӍޚय़ীኋɔ" "ᅿǢɢĿԳইษ؆ןϗƓ^ཟဖ"] :integer-tagwithfloat \ࠎ :refs-generalize :float-cutstack :boolean-savestack :chars-nth} :set-yankdup :code-wrap :floats-tagwithfloat :line-pop :input!9 '(:chars-replacefirst :print-empty? [2637 2000] :booleans-shove [\ृ \ֽ \आ]) '(:code-cons :strings-againlater [] :refs-length :refs-liftstack) :generator-againlater :point-empty? :refs-replacefirst \ᆛ [] [\ྌ \ጌ \Ʋ \ª \Д \޲ \ͺ \˯ \ස] [] 75.3046875 [491] :chars-build [0.0703125 0.0390625 0.15234375 0.08984375 0.09375 0.09765625] :chars-tagwithfloat true [\ේ \৺] :booleans-echoall :point-empty? 249.4921875 :line-circle-intersect? :environment-empty? 183.8828125 :exec-do*times :char->code :strings-sampler :string-savestack :strings->code '([2434 1937 866 167] (:boolean-echoall :string-replace :integer-dec 418430002 :refs-save) :booleans-concat [0.04296875 0.0 0.015625 0.00390625 0.109375] 54.203125) :vector-tagwithfloat \࡚ :code-do* :refs-butlast :char-savestack :strings-contains? :strings-remove [] :chars-againlater :strings-indexof '(:string-substring :booleans-concat :floats-flipstack 44.22265625 :refs-length) 127.7578125 :point-return-pop [1964 1681 4256 1220 2076 1552] :strings-swap '(:floats-flipstack :char-liftstack [false false] :ref-cutflip 945975824) ["Wࡏŭෂଽکᅽ" "፥ஆ໭" "ᄮ౜ળᄌༀ௿ঢՔઽೞ਻൷ֿɞ஀࿸ق֟ࢤߒ"] [\Ϟ \࿤ \࿃ \૙ \຿ \ໞ \ဓ] :input!7 :char-uppercase? :boolean-savestack '(616751761 [false false false true true] [58 3579 4557 484 873] :booleans-yankdup :floats-store) #{:float->boolean [] :string-pop :floats-save [\ཷ \௲ \ט \ኁ \Ҵ \ບ \Ԫ \ʰ] :booleans-rerunall :ref-yankdup :strings-flush} :ref-stackdepth :booleans-equal? :string-yankdup :integers-emptyitem? \ڞ :set-yank :char-whitespace? '(:set-subset? :code-yank :input!8 :input!8 :chars-save) [2444 1796] #{:set-flipstack :strings-cutstack :integers-flipstack :integers-storestack :chars-tagwithfloat true :integers-tagwithinteger [984 753 4506 3130 386 2662]} :booleans-tagwithinteger [4963 1368 285] [false true false true false false true true false] :line->points ["ঢ়ݎ೅ԣ໥ݐĴ" "ோᅕ¬ය" "੪൧တћᆂ࿫Ղ" "ཪ҆໭ީ൶ಛࠝ͘ယသ" "કȆཁĉର፻݆Ⴚဋࣔřࢲᎈ݉՜ၲ" "ࣣ൓๑೽སҭٰሏ୐ǱъૄǇଽƫȴ"] :input!9 :float-sign :char-liftstack :circle-savestack 464388639 \ဠ :booleans-build :integers-flush '((\Ҍ :integer-subtract ["ฎȝເ؈໪ሞ૰ኣ৫޸ټछ" "ƿՆ౴഻೿˫௩ƔၢΝኹթज़" "Qȋে༶ჿ໤૸Ǌ૓ᎈഄی؎ജಭ༷࢒" "͚ޖΥڭ؂၉״૑\r۝ࡋ๻඾"] :point-equal? :exec-echo) :exec-do*count false :chars-tagwithfloat [true]) 282.77734375 :booleans-echo :refs-contains? \ڌ \ኪ [1770 2376 1617 2328 3212 1601] :strings-new true :input!6 "ၐ೷၅๊ࠦࢇવƙ൹ץ౾࿦บᇷ඾" \໦ #{:integers-rerunall :circle-dup #{:point-savestack :input!3 [] [false true true false false] :vector-storestack :circle-concentric? 909195090 :integer-return-pop} "żͨं୽ךۉΝĶ୥ॱ݁ћѳ" '(75.19921875 :integer-multiply :exec-cutstack \ሰ (:float-echo [4363 919 2669 2572 4005] [0.0546875 0.03515625 0.15234375 0.015625 0.05859375] false :chars-do*each)) :string-last :integer≤? :string-flipstack} :set-comprehension #{[true true true false true true true true true] :string-flush "Ĩநࡦืङଇ࠮మࣘዑଥҡՇ˩ᄙಆ༠" ["͓׮̈ਫ਼ࡨ౞഼ƍ஗ྈಙࠑޱஜࣰ୨လ" "Ծೱ๏ࠍቑቋྂ" "ІսԚ"] :input!8 :strings-flush :string-contains? ["ոڑ઩" "®ࠬടཡߪ୺ዒ"]} :integers-replacefirst ["ࣖ" "౴ˎ؋ྔ˜܀ਛ፱ো͒ྤCِø"] '(:line-notequal? :refs-rest :boolean->float :generator-next [0.07421875 0.14453125 0.03515625 0.07421875 0.1484375 0.04296875 0.14453125]) :booleans-sampler :char-liftstack \² '((false :vector-shove :input!2 :input!1 :char-notequal?) :float>? :chars-dup :code-empty? :chars-set) [0.00390625 0.12109375 0.1015625 0.12109375] :chars-concat true :circle-cutflip :print-newline :vector-save :generator-next '(["औᆰ቞ᅤኺࠥٻٴޔልࣙሿᅼ྾"] :integers-echoall :generator-jump [false true false false false true false false false] true) :refs-contains? :boolean-shove :ref-print "ुמ࠙3" :float-multiply :point-return-pop :booleans-againlater [false false true false] true :line-circle-miss? :point-stackdepth :strings-shatter 738284698 :exec-pop :environment-end :input!4 [true false true false false true] \ᇩ :set-return-pop :error-empty? :set-intersection :floats-cutstack :circle-surrounds? :float-π :string-replace :float-return ["቟ෟૡĔ௭ቾţ9࢓" "ݷໞࡨ୾ࠤ"] ["ˠ֯Վ࿍࿰Ұ?ڰΜ਎̑፵౲џƌ̌ʢ" "ુ૴኏Ӑၵন၇ߧΙᆯ໗ծ" "Џ඘" "ଳ੿ᄦ኶ӥྉኃƚɼᅻᇉ೭೘ӭ׆Ƙ" "ඪ" "ᆑథ" "ᅡಝϓᆤၒͻġዊപÌֆમ઻෌" "ఐर.ʒǝ౾ଓ" "Ŭ߇ঢڏŊጔҲީ"] :code-map :integers-contains? 464085888 :boolean-return :strings-savestack :string-replacefirst '(:string-return-pop :circle-storestack :floats-generalize :code-swap 310.984375) 383.53515625 :floats-stackdepth :integer->string :floats-first [\୔ \Ϻ \ဎ \ĕ \ఏ \ࣄ \ᅃ \ǡ \ͩ] :booleans-storestack :point-liftstack [true true] :refs-flush :line-cutstack :char-liftstack :integer->asciichar 172559084 :char->float [] :string-yankdup :refs-yankdup :string-replacechar :strings-echo :generator-flipstack :vector-return [2054] :floats-byexample :chars-take :refs-echoall :integer->float '(:char-print :circle-save :chars-equal? ["wྼڒ" "জ" "5࿥hήՏ[Ⴓ" "௧" "ĳҏŎ¯ምࠎ" "Ĭኽ·ረ؎໭ጉ¶ᇼફʦႬ̿ૐൽ"] :booleans-cutstack) ["ࠛ୸ᆻҪघ੆ԅᆈβ" "຾Ȟ" "ޤชጏ༜ᅕ؏ڇΛಿཁቂቄཿໃී" "঍̛ɘಳӎጝኇ֘႙ᇖౢʙेǜ๥" "ࡷড়ሲࢊśޟޑೝޥ" "ܰ஥ɱϼ̪۟>Ǔ]੔ڭ૛፭ຠᅬဿځΙභੵ" "ΡږӋࢫಘ௕ລ"] :strings-tagwithinteger 249283346 "஛܊ހᅃ" false "ÈতȘෆߪ̲਒" 430266047 129.35546875]
  :bindings{:input!1 :ref-forget, :input!2 :ref-againlater, :input!3 :boolean-tagwithinteger, :input!4 true, :input!5 :vector-swap, :input!6 :booleans-emptyitem?, :input!7 true, :input!8 :set-notequal?, :input!9 :chars-yankdup}



  }
])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

