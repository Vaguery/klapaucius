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
              (println (str "\n >> generator: " (pr-str (count (u/get-stack s :generator)))
                            ; "\n >> exec: " (pr-str (u/get-stack s :exec))
                            ; "\n >> integer: " (pr-str (u/get-stack s :integer))
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
  ;; times out
    {:program [:line-store #{284.4296875 :exec-flipstack [] :char-cutstack #{:input!2 :line-save [0.09375 0.0546875] #{:input!3 269536184 180528029 330538908 :string<? :line<-points :refs-contains? :refs-portion} :booleans-take :code-container :float->code :chars-flush} [false] :string-emptystring? :input!6} :integers-rerunall [1130 822] :char-savestack :chars-swap :booleans-generalizeall :chars-notequal? :exec-flipstack :exec-yank "Ǳقሃߨအ༺ࢋŋܺ" :strings-indexof :floats-first :integer-many :booleans-conj :input!5 :booleans-swap :strings-reverse :point-againlater :string-containschar? 87.05078125 true :refs-save :vector-pop :code-save :ref-stackdepth :strings-conj :line-store :float>? \੸ :set-storestack :ref-cutflip 216.48046875 :generator-echoall :integers-do*each :boolean-not :set-save :input!3 [543 3080 4459] :generator-storestack :input!7 [true true false true true false false] :ref-lookup :exec-later :code-member? :refs-yank :line->code :strings-concat :char-yank :vector-rest :float-return-pop [] :refs-set :code-points :code-do :chars-return :ref-pop :integers-shove :integers-cutstack :chars-emptyitem? \ࢇ ["ζĳȒ൏஘ͣጝੲ٘෡टˢ૟༇؟ᄙၓ"] #{224.5703125 ["၁ᇝ࣊૙ඌǸูѨWͩฉו" "ٷࣄ੷ؔۤ" "ར໎ͯ୭ѬØݯ´ީীኙᄙ׵ஊ"] :ref-dump [] '(:float≤? ["ԉ" "ԽǱᇙ๬ೢ्ᆉ"] :char-dup (:boolean-swap :integers-emptyitem? :vector-replacefirst :generator-stepper [0.09375 0.01171875 0.0234375 0.0859375 0.1171875 0.11328125 0.04296875 0.11328125 0.1171875]) 356.37109375) 138038745 :integer-savestack :vector-refilter} :input!2 :chars-echoall :float-arccosine :integer-multiply [] :floats-empty? [true] :ref-equal? "߼Գॣǹ፺ж໏ೌ" :booleans-concat [\ᄵ \Ö \ʡ \ᅼ \ხ \ॴ \Ȉ] ["௖ӟር͢༿ይੀ࣐ؽਏגԅღϕ" "௿ࠝᇨማᇟᆹมmఉ່ܿ" "ኽ়ఋഗঙွዺ৘Łثഞᇎ࠼" "ඹႈࢴПက]ఢኜౌ௄ؠ૮ጦՈ" "࠯Ƃჯ೩۟ᎏࠟԯଊઓၑϷ๴શ൷ঊǔۈ੝ઃ" "ƶ" "Χ࢑זፅၽՙچ৬৥಍ျསശɢཱྀ٤ȏξࠖ" "ൗۢվ"] :environment-begin :vector-butlast :ref-echo [\ํ \૸ \њ \Ռ \ᇹ] :refs-new [\ǋ \਎ \൅ \೯ \ü \౯] :code-print :refs-equal? :chars-swap '(false :booleans-shatter ["ቜ৥ຈශ࠲೽౉ɒ" "޹ऱ̹॔ۦቚӿᇓ౉቙઎Ɠ࢛ചˌ" "Ĝணో̛౾ᎏఱጺ̪౽ҤȬٮગᎉᆶਮҝ" "ܳዶඝᇑѻట༒থڲ۽ॏ໘່ఔਸ" "ႤഢഈߜമԀፏᇯɢ஫:ࣦከכಬҦ෠፤" "ໆࣄʵЅ८ናᅓ" "྽ޮ࠴ୈฒ໼རዩԚ׵ႜΟ഍"] :code-do* []) :char-echoall [0.03515625 0.0625 0.0625] :boolean->code :chars-reverse :circle->code :floats-replacefirst [] [\࿶ \׆ \࠳ \ش \ॖ \ʥ \๿ \Ҍ \ɥ] [709] :input!10 false [0.04296875 0.0 0.015625 0.09765625 0.11328125 0.0546875 0.1484375] 499301586 :chars-swap :floats-shatter 200.2265625 :integers-later '(:floats-save 899111370 99.4453125 :ref-clear (:input!3 :booleans-dup \ֿ :exec-noop :string-max)) :strings-contains? \ࢌ :exec-echoall :floats-later ["̉ᄋܚ°ᄱGڕԻʹ଄෡้ෂÉ։Հ๔ၽ˄" "ှሢݏॊुቾƺڡ;Èં༰óᄷಃĭାއ˝࢚" "਺ӳ೏qs৶নቡٻࣀÁႅʗ঄ǵѱ႖ن" "रĄ" "ํࢷ߬ሰ" "ࢍ࿁໢֐ఎരᄖ" "Йለַ࢝ࡕ\fధᆐѭ૩੒ᇃጁ೏࡟Ϥ" "ѿ۠" "ሢ࠰ᅉ፰൛ைҍ"] [\࡛] ["ᅛ઼໖ࡾ݂৞ဥໄÑ൜ᆿၬօɨ" "Β͵୧ྲྀ" "໗ȱྸ໫੄ະ" "B቏ࢿٯ˝঻Եॲኸհኅངຶ" "ࢸഞƹॅଇੰ஄୘-ʀਅਙ΍ࣗ" "ඒဈၛ࿰ԁ̂ฬࣸර୫" "୧aÞ፵ਥɒŀ" "֑ǣׅ" "ลኡං̄ᇅ̽ྟ਻াෲ೚৒ጄ"] :floats-length :integer>? :string-replacefirst "̇ᄫറ६ǻռ໯༛ኖҀቮ\tཱིུ܀௳" [false false true true false false] :floats-return :chars-equal? false [1635 2275] :code-position :vector-nth :circle-equal? '(:intsign->boolean :chars-first :chars-conj :integers-portion \ਨ) :integers-reverse :ref-savestack :input!6 :float-notequal? :booleans-take :exec-echoall [false true true true true true false false true] [true false false true true false true false] "ধࡎ႑T÷ኞ֥ढిౖ࡝̔๺ௐ¾į˄२" '(:circle-shove :boolean-flush \຃ [false true] 188.0078125) \ң \༳ :integers-new [] :string->integer true :circle-cutstack :circle-equal? :set-difference :ref-equal? :ref-later :circle-save #{:set-pop :integers-nth :point-inside? 675494878 :set-equal? :line-savestack :environment-empty? :booleans-replace} :integers-save :booleans-emptyitem? false :line-empty? \+ :refs-echo #{:exec-flush :float-empty? #{[false false true true false true false false] 593296268 :integers-return :char-rerunall :booleans-rest 363.46484375 :line-empty? :floats-shatter} #{:char-echo [0.09375 0.0234375 0.1015625 0.0078125 0.125 0.12890625 0.078125] '(:integers-return :string-return-pop [0.125 0.11328125 0.09375 0.078125 0.078125 0.0546875 0.12109375 0.11328125] \շ :string-savestack) :boolean-shove :input!1 :exec-equal? :vector-length :boolean->signedint} true :float->code :generator->code :boolean->signedint} '([] :circle-concentric? [0.078125 0.1015625 0.0 0.14453125 0.01171875 0.09765625] #{:input!2 :refs-return :code-if :float-cutstack :generator-again :input!4 ([3662 2434 3213 4348 59 4548] [false false false true true false] [0.09765625 0.08203125 0.078125 0.015625 0.04296875 0.09765625 0.109375] :code-echo [false false]) \઻} true) :point-yank '("رǻ" 175.16796875 :string-nth (:ref-dup false :integer≤? [0.0234375 0.0546875] :input!10) :line-intersection) 373.47265625 :environment-end :boolean->float :ref-swap [false] [0.0703125 0.08984375 0.0859375 0.07421875 0.12890625 0.05078125 0.015625 0.01953125 0.12890625] false :refs-byexample :floats-savestack [false true true true true false true true false] '(:exec-rerunall :float-ln1p :vector-remove :char-flush :strings-indexof) :exec-if [0.0625 0.0390625 0.015625] '(926864678 :generator-counter ["̈஛੒ढ़ᇿ໨৖ʌʟኺ࿦ಇॄ" "୩ۑக࿴๔ǪኖȏතϾ஛̰" "ɴၪf၂ႸπȌڑ୷൛׭ޮ၎हழఈ֐" "଼౵φ௨࿀տ׻ኢᇥéᆚȉჿጣ๳ಜ" "ซࡳ͆ӵ੘ኸ˶ïࢅႴॾ6˗ᆼ౑܇ײ፛" "తཬǚᇉࡍʲओсՊቚМྤܛ፬ෆ೼ච" "஄{ෛਝс౶፻" "ቔɫ༴ආེ"] #{:ref-fullquote :strings-empty? [true true] :integer-yank 440090793 \ᆲ [0.0234375 0.0390625 0.1171875 0.03515625 0.01171875 0.0859375 0.12890625 0.0234375 0.09375] :booleans-save} [false]) [false false true] ["͖֓লбຨॵ঎ӏ፧" "ᅬ๸ිԥNບ҅sUʓͩࡾ"] [true false false false true false true true false] 725468580 "࿒፲ُጜøفच૬̺ࡩ࿯ᄰ࢕ത" :input!10 :float-againlater \່ :chars-replace :set-dup :input!2 :input!1 :booleans-cutstack :ref-echo :refs-empty? 2.97265625 true :input!1 :strings-flush [] [0.078125 0.06640625 0.0546875 0.109375 0.0234375 0.05078125 0.0390625 0.1015625] :refs-cycler ["ܦƉ" "ࠨᅯгၷ঺ၙၯԷଜፈ&ť໥αǈလஃ÷ക୯" "ਿ୾ɍጶ౭ဝ࿗ࡊᄜᎊฐǯбᅠᆆі@ǉ" "೶઱ǚ୕ა؟ڍ̡ਃ" "४ڤᇤ࣓Ⱥፖѳ࿍̅ཫ͂ᆥᆿί೺" "٘঄໌੉˖པ୧࿞ቊఔഥฏ௖ඒᄃँᆡѾɭ" "ଅųĂᆨҺዟඏޓĎጶƽ֋ತথڢපቇ൝ഽ" "ՙĨண" "ഄɽ৾چ"] :floats-pop :line-flipstack :floats-store [4584 1102 3370 4345 2841 3006] :input!7 "ᄲڄ޲҉ࣨሔ֐׾ܓลᆾ෵௪" :integers->code]
  :bindings {:input!2 :refs-return, :input!9 :string-yankdup, :input!3 :char-print, :input!10 :generator-next, :input!1 [\ॴ \൧ \௻ \࿀ \޾ \ј \ƴ], :input!8 '((:code-do*count :integer-store ([0.046875 0.01171875 0.046875 0.09375] :float-divide "֜շғ്ޠ" ["ୢ๶ݏፉǣξኑ" "ኳ̙ฑ̲Ƣ઻ዋǀ˱༆ആશ໳ќଚ໋" "׳̶ͤ೨ޭ๾ߣᇌዳഈྲྀျૄएቂ" "༐ల૸׌ౖሣๅ" "ி߶̾ฬᎍúୗᆂႳ" "৓ȫάૌ׭Ķ૨౯ဈࠔ८"])) [4464] :code-rest [2697 1051 2238 3369 3653] :chars-generalizeall), :input!4 :vector-notequal?, :input!7 ["˷ࣈ·ܰࡋআਣݶǜ੡ໂቍ" "ÐଶȽಚ೑ଇ۸ጵȻ" "Ԑ࠷ᆇ΋ݫࣶ࿅ᇷብຕߛ" "ᅷࡐሤ߂ᇣ፶̷ኡ" "σ۪੼ͪ࿲Ԓ೰ŘຄϏ޶ݦ" "཭ठඍט˿ټઅ" "ąى༼ጝ൯ҁಟৌ഻İᆲ՝ᄕ݋" "ȩ຀ധࢍ" "ƿਾཛശɉ౧ଝ"], :input!5 :float-mod, :input!6 [2980 470 3700 3681 3457]}


  }
])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

