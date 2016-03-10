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
              (println (str "\n >> generator: " (pr-str (u/get-stack s :generator))
                            ; "\n >> exec: " (pr-str (u/get-stack s :exec))
                            "\n >> error: " (pr-str (u/get-stack s :error))
                            "\n\n"
                            (pr-str (u/peek-at-stack s :log))
                            "\n" (get-in s [:config :max-collection-size])
                            
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
    {:program [:boolean->signedfloat :integers-contains? '(:input!3 :generator-shove "਌ʟǓӁʓ༘ͩ" ["ݑݸಪǖ઎́ጯٜƃໞ౥߅" "î׏ÁѸฌ" "ረ" "ૺጲ˓ਗ௉Ⴢ࿇໯ԛయԒŏϚယ" "ۋᅸڑѭᇟϹňෟ༘Ώ༓ఴ"] :string≤?) :circle->code :string-stackdepth :set-yank [true] '(:integer-subtract [\࿸ \ې \ً \ࡲ \̪ \క \઱ \ᅴ \౰] "ࣇᇦஈƛ࿔Ⴘ࿪" :string-indexofchar :line-save) [false true] 336.10546875 '(["ٰષƈࠔȍ༭ंເઐ஘མـњͿ" "ڴ٩" "ܠώͺଘበൿᄯܥટພɣᆇّ¡аడृ፨ࠇ" "݄ǟߣф̹Ǉిྖܩᇹഥ+ፄΞ඀๰" "͍ȹܵ" "ļࡗӈྜߧ࣐ญβሎ߿ኹڶᅁ" "ࡅΛᇋါ"] (:vector-echo :booleans->code :set-shove :vector-first :integer-empty?) [988 2709 1740 846 194] 200.3046875 :vector-contains?) #{:input!3 :ref-pop true :ref-print :code-stackdepth 265477567 ["ራྞ૗ۛɇ໚Ӗጀ©ግႇ" "ࠄǿ̌Ѣਲ਼ቼٴ඿ࠀڈׄΦѾ࿽็ߺԌᅶབ"] :chars-first} :chars-echo :vector-take :line-circle-intersect? 340595335 :generator-jump :float-divide [529 1624 254 132 1678 3425 1083 3222 422] :chars-shove 62778761 :input!5 :float-yankdup :chars-againlater :integer->float false :floats-return true 251.3671875 '(:string-containschar? #{120332878 [3159 2360 4059 1509 2462 2827 3958 2939 1563] :integer-store 343540709 :char≤? \ຈ :strings-emptyitem? :booleans-print} ["࿋ӌෙϤ໣ğኯฉʺݱ௥଻ጂ࿘ᇀ໨ࠈ಴" "ဆӓWᅦ૧" "۪ഘ̈ፌ࣫ʮ଍ސ६ཙख़਀ბၠ" "઒ൎɔ༢ኼᇠӤ൮ེ͗൭အأດོߞᅲ௲ѴΠ" "ͬ"] [] :circle-againlater) :boolean-xor :circle->points :circle-yank \ტ 186.8125 :float-π \փ :strings->code \௓ :exec-save '(:integer-yankdup :refs-conj :circle-coincide? :vector-nth :integers-conj) :booleans-flipstack :refs-savestack #{:string-cutstack :integer-divide #{[0.02734375 0.0703125 0.125 0.06640625 0.0546875 0.1328125 0.13671875] :vector-butlast :refs-remove :code-extract :integers-length :floats-generalize :floats-rotate \ɚ} :integers-return :floats-equal? :float->asciichar :floats-yankdup false} :integer-sign [] :integer-empty? 259.7421875 true :float-inc :code-do*count [\֡ \ள \ᅇ] :code-if :set-cutstack :chars-remove 191.875 :floats-equal? :integer≥? :exec-shove :point-online? :exec-while :char-save :char-againlater :char-notequal? [\ಸ \ሖ \࿋ \̷ \ാ \ࢨ \ຂ \஬] ["̩ൽƾኴ࿅ŜᆈᆉѰ࠺ǏᄗϛĘংࢥੜ̠ࠖች" "ܢኧීంි¦ɵ֟dެৌ༪෵" "๿ɹ፵੗˔঵༅ڧʩ੩" "൙εᇠᎌမʩӥ๗ቓ๭୍ѝየ෠໬৭їٕ" "ᄺӺݗ˰ߣд૖ॲ٤ᆽϐ༕ယԛጺ²Ν໮ࢰࡑ"] :integers-store :floats-replacefirst :ref-return-pop [true true true false true true true true] :booleans-contains? ["ʿ" "׽࿇ၿނ࿝Ѐੵலéӥجഗ঴˹௩ల"] [1142 4133 4807 989 2265 1576 1702] "௸؉݂ډшᇜၞҲŇǂЏᄖᇿཋĘʕП፶" :line-parallel? :booleans-build :float->code [false false true true false false] :vector-print ["ܥȽ৆ହߘ୕ങ፤e૴Ү"] 390.16796875 '([\௝  \௉ \ࢳ] :code-container [1085] \৐ :chars-new) '(:float-ln 594962986 :char-echo :line-equal? (:code-subst 324.7890625 671199145 :char-later [true true false false])) :code-extract ["ᇢ" "ഀ̳" "Ғí˚ᇈkࠝፚ෡̤݉̀࠰ۃफ=*ඈ઩" "ࠑݦᆾནኂ¬ݩफ़ˏઝݍ୿஝Ȳѩᅢঅ" "஁፧ԃா" "গΐሆऒӮုԓఁ" "ሡ໸Ė့"] :float->string :integer-rotate :line-equal? :refs-cutflip :exec-noop :integer-store :char-letter? :line<-points #{["วྶ" "ϓ़ȅນჸ೵¤࢓࿱" "ፘରअ̱ಇ߆ಏްၧɽᇪຝজ༸౯" "ۢȘ௦ఃሯӽ፮ࢹ୰ಿᇛ้ྡྷ້ႀ̰޾ū" "ͬټڠˊరྵҰ" "ုয໾ᇍᅟ༦νࢻНࠖ቞छᇷ" "Ը൨୔ƻԉᅽළ႘Ηযร୊րڕ" "ྊ౛೉ൄਫ"] [0.046875] :floats-cutflip ["ࡊ۵฿ਞ୍৫ௌၞւതԌ੍ሆጙરȬ" "วऻׂຩ΍ݶภన ИĂߧܑϳ߃ᇋ൫ɀ" "ঠ෋ڒ௽୉ȚËۻᄵֳՊɓ٤؎བິర" "఺ॷጿቜథ༠ภʬࣝVּͣ঺ਖ਼" "ډݠଘė႟ě၌ቶِڰÛยऀᄁ౹ùЕ፸ഛ"] :string-storestack \ښ :set->code \஝} :char-notequal? :line-againlater :set-superset? [false false false false false true] true :strings-generalize :strings-cutflip :integers-flipstack :code-savestack :booleans-replace :string-nth 682793672 :ref-forget :circle-save :floats-print '(:strings-generalizeall :booleans-build :integers-remove :float-max 116073675) :line->points :vector-portion :float->string true [] :set-flipstack '(:float-echo :set-echo :refs-byexample :code-extract :integers-savestack) :float-arctangent \ண \ຼ :char-swap [\ࢊ \߯ \୶ \໾ \9 \৲ \ګ] :line-swap :code-pop "ᆼଣ༌Õ¨ሠᄃۀ෿খຈ" :vector-contains? :integers-shove :line-circle-intersections true #{:string->integer :set-union :input!3 :refs-later :input!8 :integer-add false :booleans-replace} ["ࢽ" "஬຾໽" "੸~Ч" "Ӳݤஸ{ƁဈݳऄቅՐȡୢӸೄቕ"] :circle-againlater :string-storestack ["۲႙৖নࠬി፝ᇤཪ઴༩ಚઅᎇĀ" "܂ำ༦์བྷ፧̪" "਷ࡅಜ୆͘ӧਔ౧အ۝%ٛڥ" "ሜ" "੟શ" "௾ҌᎁۿࣟᇝಛશԠৼାቛᄴҖ࿘෬ڴ"] ["ъಪഖဴຨ༞࠲ިΗ" "ບ˩రမಫټा࠱Ҹੈӛލ" "ო̀ү͎ള֓ᅔͮম༕۸ϐ߅࣢௾"] :integers-nth :code-shove :integer-cutstack 517551283 :chars-store #{[false true false false true false false] :integer-subtract :floats-byexample [2356 916 3841 4407] 345.60546875 308.71484375 :chars-save :integer<?} :print-stackdepth :string-shove :char-letter? :booleans-rest #{:booleans-do*each :vector-new [true true true false true true] :chars-build :chars-flipstack :vector-nth :exec-swap :input!5} :refs-first :integers-empty? true [0.0625 0.10546875 0.08203125 0.1171875 0.140625 0.04296875] [false false true false false] :input!9 972726270 :environment-begin '(:booleans-savestack :input!8 (:ref-fullquote #{"໛؛ޱ" :exec-if [4840 4310] \౏ :generator-again "࿝æྊီǣ๗੎঱Ⴓ࿩ඟદߓचƗႍਈļහ؄" "͙ઔ୺ଽዒᇫཉෞۮ቗ख൐οٿਰԻȸڔኵ"} :input!6 :point-yank :integers-build) :booleans-contains? [2543 1576 4037 4321]) :boolean-rotate [0.10546875 0.1328125 0.11328125 0.09375 0.0859375 0.09375 0.0234375] :chars-stackdepth [0.0078125 0.00390625 0.07421875 0.015625 0.12109375 0.09765625 0.08203125] :ref-dup [true true] true :vector-replacefirst '(362.6640625 :code-comprehension :integer-subtract :float->integer :set-store) :integer-max ["ঔኀ"] [0.0625 0.0859375 0.0703125 0.11328125] :string-removechar :float-savestack 513497883 :strings-first ["ሂ࿥አ΁ÿ์֨͛ᇪࠡ႗඘͐ৃጝҺዳዤļ" "ːሯ̌ᇌપ።ѯጌаവϬݵ฻ၳ༲͉ؔĜת" "֥Ȋɲ࿘ࡹƵ୘ྺƝ༦ņrচ॒೯ᇯଛ" "ࣝ႗፤৭ቆ۰ஜଥቍॢΖలĶߙڤ" "ኄٵ̌չମוଔལ࡝ያඋႜ౸čଖ࿴ጟչ"] :booleans-new [] :integers-cycler ["ܹÖຓඇڔဇྷ؄ऱ౔Է/ťᅡ஝" "ۃထࢾƱॗ෸शƝ" "ຢҹഓං਷౔"] #{\ࠅ :code-pop :float-store :refs-flipstack 326.44921875 false "༣ҷՈ္ݮսî൐ཏँÚ৲ெළࡩब̶७೓ढ" :float-inc} "ͱ๑፥๱ݚจҐΈૈᇆ" [0.0703125] :push-bindingset :chars-return :chars-indexof :input!2 :char->integer [false false true false true true false true true] '(:line-parallel? \Ѕ :input!1 :code-do*range 243022774) [] :ref-stackdepth :integer≤? :integer-yankdup :input!6 \ࣤ [false true true true false false] :string-flipstack [] :generator-stepper :floats-butlast :float-cosine ["Û࣯ո࿏ْЛͫҾ஍୮ᇧ጑༪ቪv࡯" "೅ౙªյ݂฾H୻ᇇђဿ" "ߗߠ"] :char->code]
  :bindings {:input!2 :generator-jump, :input!9 :integers-replacefirst, :input!3 \৑, :input!10 :integer-yankdup, :input!1 ["ᄈѨ" "੼়फ^¥ऊͦࣕ઻ཡ೽Ҫ෤࢝ຩ"], :input!8 :exec-rotate, :input!4 :integers-equal?, :input!7 [\൞ \૪ \ؑ \௔ \ʬ \ጓ \਩], :input!5 :chars-length, :input!6 :chars-shatter}


  }
])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

