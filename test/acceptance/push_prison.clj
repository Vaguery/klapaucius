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
                            "\n >> int: " (pr-str (u/get-stack s :integer))
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
    {:program [:floats-portion '(:integers-concat :booleans-return :integers-build :strings-take 456087234) :string-first :exec-equal? :integers-butlast 141525717 \Ⴣ 292720556 #{\ඣ :exec-return-pop #{:strings-generalize :integers-equal? "༠௦˞ƪŠቒཨ" [0.07421875] ["\tنɍȐԙũཾ๸Ч๛֬˞ଢ଼ڂAÎႀō" "዇&ያ࿐ҽ޷Γ௞ጹຮӥԙˍໞӏǡܾ" "ڳພࣂ߶߇ӄѦK࣎ഡ໕ெ׌ይԠ௺ྩ" "ڕൄใࣅ၆וŲ׽჏࿞" "౛ಟЭᇁࢣჾųʆፌ" "ጻۺय़ဍ๿vᆀႜటಣභቬ" "ࣙංቖልች܇ၛࣿ߀"] [\ມ \܋ \ဃ \´ \൑ \੡ \ɀ] "Ӫ߷ኋүହই຿֢࿌෨ר৉˱" :string-yank} 238964505 #demo.examples.plane_geometry.definitions.Point{:x 3.4842578125e2, :y 6.7112444e7} :float-inc :circle->points :input!6} [false true false true true false true true] :vector-notequal? :char-equal? "ಾഝኙ͕ࡇઐȈ፶ʶสᆌ" "ങ׻ಎੳԷ௹ኑٜ፮ϭҸኦ" :chars-byexample :print-space 126.65625 :strings-build :float-π "ࣙ໔ຟΒڧޏࡽ̓" [false true false true false true true false true] :booleans-conj :integers-nth 301.765625 :input!8 :line-print false :integers-pop :strings-emptyitem? 133.6875 :set-return-pop :strings-concat :string-replacefirstchar :code-do*range 718721990 :integer-notequal? :circle-concentric? [false true true true false] [0.1328125 0.125] :string-last [3802 1919 4647 1799 4929] :char->integer :circle<-points "਎áೞΡ૚ţლ౟ఎྒÖݤሴຣ˾" :integer-max \ᇷ [\੉ \০] "ዿȢݏϵࣜ൳Ӈறফ፫޽ൟᄞቩըஷ" :vector-concat :floats-emptyitem? :booleans-replacefirst :exec-while 732943118 #{:string->float :floats-do*each "ĭᄨܖ፫پśࡂെҺവ" 413295506 :floats-indexof 293.96484375 #demo.examples.plane_geometry.definitions.Point{:x 2.19578125e2, :y 4.39627532e8} ["ƀ֪୮¨஥ཐЏ"]} :integer-sign :vector-print ["Â෇ඞ" "ລŵȞदĂ๯" "රݦጪ༷લൊ௬৴ᆴےᄢ" "ቿፒूච౜Ϛñິቧ්̨ڋࣄુ" "xՠჃ෍૤ǽ" "Қि੊ዺ፟ૼ໖" "(ુശʋ¥" "Ȑള˼"] "ཽ܈མઁࣔ༔໙޶" :string-occurrencesofchar [false false false true false true true] [4726] #demo.examples.plane_geometry.definitions.Line{:p1 #demo.examples.plane_geometry.definitions.Point{:x 1.28671875e1, :y 7.47549963e8}, :p2 #demo.examples.plane_geometry.definitions.Point{:x 3.5535546875e2, :y 5.84106089e8}} :integer-inc "຾ѧŁʃ" :boolean-flipstack ["ʸ" "Uಂࠜڳ׬ॏࠅۙः֠" "ጵক߁ቺĻอ઴঩ᅀŏֱࠤࠜ" "ึᅍߧ੥ჭಪੱςٻѭඣ௛ಪ཈ဣዹᆇ႑ಆ" "Ƨఙʹۑ΀ՊᅛՑࠁྱ෪ීŘԔ" "ھ༦໔ഢʷᄒ൜ഴөߋ" "фۅၟ࣬Vዼ๘ҾT" "෰Ԧህ"] :booleans-pop :chars-contains? :circle-flush '(:print-empty? [0.1171875 0.109375 0.11328125 0.05859375 0.12890625 0.0703125 0.08984375 0.04296875] :floats-stackdepth :chars-pop 224655610) \ᎋ :integer->asciichar 197.3046875 \ጴ :integer-add :booleans-butlast :line->code :line-intersection 98.5859375 ["ነиǄݺܲ˹А" "৮༕౶ਪड़ಾଛ" "඗ۦƇŞनฺȬը" "ஷݬၪʾ"] [0.00390625 0.1171875 0.0234375] 548908158 ["Ⴟቋ्óဝጶրᅄዬҏׁࣳླՍH" "ä஍౪ᎋ" "ɽീেøާນݦ๶tࡹљԪ%ΐܙාǘ྽ટ"] :booleans-contains? '(#{:vector-replace :line-equal? :code-list #demo.examples.plane_geometry.definitions.Point{:x 3.4290625e2, :y 4.92074276e8} "Ǚ୸ޑঐ༎ÜɋᇇႤͪဳ༬ͦ૬ှ٭" [3545 378 1033 1194 513 1633] [0.1328125 0.125 0.1171875 0.09375 0.03515625 0.15234375 0.12109375 0.10546875] #{313.65625 :float-dup :circle-separate? :integers-portion 295.04296875 :booleans-yank :exec-notequal? :strings-new}} 282622270 false :integer-multiply #demo.examples.plane_geometry.definitions.Circle{:origin #demo.examples.plane_geometry.definitions.Point{:x 1.84234375e2, :y 4.62249321e8}, :edgepoint #demo.examples.plane_geometry.definitions.Point{:x 3.1651171875e2, :y 7.17121089e8}}) #{:strings-cutflip "஽࡝̪௨Ҕ݋໲" #demo.examples.plane_geometry.definitions.Point{:x 2.07953125e2, :y 2.36649569e8} :code-do*times '(:exec-swap :chars-new #demo.examples.plane_geometry.definitions.Point{:x 8.6078125e1, :y 2.2908994e8} [\࿳ \փ \߅ \ଢ \Ⴧ \ਥ \ގ] #demo.examples.plane_geometry.definitions.Circle{:origin #demo.examples.plane_geometry.definitions.Point{:x 1.66875e1, :y 3.90148381e8}, :edgepoint #demo.examples.plane_geometry.definitions.Point{:x 1.6958203125e2, :y 5.77242596e8}}) :float-arccosine ["Ԅኌዀጊൠౖᅴʜ" "ўѻͧࣔྋࡋ" "েݼ፵ࢹሒ!਄ຟᄋ" "Ƌ̆ውܤኢᆠɜۭౚЕ࢐፟" "ቑဩૈል" "ӎ୆ࡴદ୕ుሊǘ၆ᅍۖ٭˛፺ͩЪ׶" "࿁ຖထϵമᄲݘझ௯෡ݜዒ๟ܛ෡ᆾױȖ"] :code-rest} ":ՄͲٔ" :code-notequal? :exec-return :vector-byexample ["κ౎৴భลಯԎŝృ˷ၹ˛൜፷" "ྍ෶࣏ޜਤԯਕၼພયᇺாທИ၄෿" "ٽǷ෡̤ᄝ࿺ിำ"] :chars-notequal? \՜ [\ః \ხ \ʉ \I] :input!5 \૕ [\อ \ઈ \ၷ \ۄ \ؔ] :float-return-pop :float<? #demo.examples.plane_geometry.definitions.Circle{:origin #demo.examples.plane_geometry.definitions.Point{:x 2.561328125e1, :y 3.78989595e8}, :edgepoint #demo.examples.plane_geometry.definitions.Point{:x 3.4484375e1, :y 4.69554923e8}} :line-swap :exec-while :strings-rotate :string≥? #{:code-cons false [0.15234375 0.03125 0.1171875 0.0078125 0.02734375 0.1328125 0.08984375] :booleans-portion '(250.7890625 :integer≤? \ώ 332891938 (:point-yank ["Օߦཞट੒፦ವ۷ֺмਜ਼ၻۯԐ֙" "ೃဇቾࣃሖೃ¨ߏજ" "ȏjೲ؆ԚͿ·׍" "ኃ֬ݙ઴፬ԙڈ`ዾ}ϐޫ" "ୡይصࡋ໶လቯ" "ၷᇬڒกࢾ໫" "შሳѾആȵዜᇺ૑ԏѹ"] [\ê \਷ \ѿ] :circle-shove :point-swap)) [\ᇜ \܇ \ݢ \ෲ \ಲ \ጡ \੹ \ݫ \ሂ] :string-first} :vector->code :exec-pop "ԒۛķᎈȀ" :chars-occurrencesof :exec-do*times false :integer-stackdepth \у [true true false true true true true] :input!1 :integer-swap "ܳ෦ႇᅗȚࢣӪᎅՀᅗࣨዶლჸөŦѻ" [421] :string-flipstack :input!1 :integer-uniform :integer-notequal? \Ħ :booleans-rotate :string-length :float-swap 798561586 :point-flush #demo.examples.plane_geometry.definitions.Circle{:origin #demo.examples.plane_geometry.definitions.Point{:x 3.8633203125e2, :y 5.8337035e7}, :edgepoint #demo.examples.plane_geometry.definitions.Point{:x 2.3303125e2, :y 5.93227601e8}} #demo.examples.plane_geometry.definitions.Circle{:origin #demo.examples.plane_geometry.definitions.Point{:x 1.102578125e2, :y 7.23851384e8}, :edgepoint #demo.examples.plane_geometry.definitions.Point{:x 3.6945703125e2, :y 6.76382777e8}} :boolean-pop [false] #{[\ቴ \භ \Ϧ \ᅲ \ఠ \቎ \ሏ \ภ] :floats-swap "ඉႾ̮लᇢఖk૳" :string-replacechar true 521158251 :floats-generalize :booleans-concat} :string->float :circle-rotate 326.9921875 #demo.examples.plane_geometry.definitions.Circle{:origin #demo.examples.plane_geometry.definitions.Point{:x 2.0904296875e2, :y 7.0404222e7}, :edgepoint #demo.examples.plane_geometry.definitions.Point{:x 1.8714453125e2, :y 7.77287775e8}} :integers-yank :line-stackdepth :integers-length [4792] :floats-butlast :char<? :booleans-take :integers-byexample \ॠ :string-emptystring? :integers->code "ೃ็್ధᅹ܉ࣼᆟ༨ྡྷಏᆱዡ" :char-min :booleans-replacefirst [\๲ \ሣ \࠯] :string-indexofchar #demo.examples.plane_geometry.definitions.Circle{:origin #demo.examples.plane_geometry.definitions.Point{:x 3.3084375e2, :y 7.05607715e8}, :edgepoint #demo.examples.plane_geometry.definitions.Point{:x 3.4898828125e2, :y 2.915863e8}} ["ƶশྖି။ฌ౿ᅲ੽ኂ൤" "ཅڟ̥ሃᆀ፱ॅչֻӄਤ©शๆȀຠô" "͛ॻ͔" "ڢ॒த༫୐୅ཎƸპૣ٪ၾ኏ŻΩ໊ृ௧ыᄫ" "ኋ"] :vector-shatter :floats-first :set-flipstack '([0.02734375 0.03515625 0.0859375 0.140625 0.03515625 0.0078125 0.08984375 0.10546875] :exec-do*range :floats-first [true true true true true true false false] :strings-pop) :string-reverse [278 4105] '([0.13671875 0.1015625 0.05859375 0.1171875] ((206234271 #{:set-flipstack #{246.21484375 :circle-surrounds? (#demo.examples.plane_geometry.definitions.Point{:x 1.6071875e2, :y 3.28551103e8} [false false true false false] :integer≥? :input!6 #demo.examples.plane_geometry.definitions.Point{:x 2.549609375e1, :y 5.99435793e8}) :float-cutstack #demo.examples.plane_geometry.definitions.Line{:p1 #demo.examples.plane_geometry.definitions.Point{:x 1.12796875e2, :y 1.82607532e8}, :p2 #demo.examples.plane_geometry.definitions.Point{:x 3.42578125, :y 2.21711798e8}} :integer-lots :exec-return :vector-return} :floats-new \ᇍ :chars-byexample :boolean->code #demo.examples.plane_geometry.definitions.Point{:x 1.8791015625e2, :y 6.43682629e8} :booleans-conj} ["ོඨࢥᅚଷຯ൦ྥօሐি൉" "ͳଜทvӜ਷ൕၝފਊأኡԷմ" "ጦјᆞ"] :input!5 :vector-flush) :float-print :floats-reverse #demo.examples.plane_geometry.definitions.Line{:p1 #demo.examples.plane_geometry.definitions.Point{:x 2.1654296875e2, :y 5.70458579e8}, :p2 #demo.examples.plane_geometry.definitions.Point{:x 2.1613671875e2, :y 2.5029751e7}} ["ኚন׿ࡒʼŒᅑХถ፭ඬƳ׬୶ᄆጲጢ" "ȫܘᇧᅾՏຊ೉఩ਭ࿊Щ" "iၷีڣဇ൶੫ທढɘُ୲෬୺ҭ"]) :boolean-or \˲ \વ) [0.11328125 0.03515625 0.01953125 0.10546875] [0.02734375 0.0078125] :char-yank #demo.examples.plane_geometry.definitions.Line{:p1 #demo.examples.plane_geometry.definitions.Point{:x 2.1964453125e2, :y 7.55083378e8}, :p2 #demo.examples.plane_geometry.definitions.Point{:x 8.986328125e1, :y 9.9155837e8}} :chars-yankdup \ǫ :vector-yank #demo.examples.plane_geometry.definitions.Point{:x 6.31953125e1, :y 8.58095607e8} :point-shove [false true true true true] 336.10546875 :float-rotate :code-if :strings-concat #demo.examples.plane_geometry.definitions.Point{:x 5.5859375, :y 9.03378367e8} :integer-sign :chars-reverse :float-return-pop #demo.examples.plane_geometry.definitions.Point{:x 2.46703125e2, :y 4.3328135e7} [0.01171875] "هԹహԸؤಚ๘՘ༀᄅٓတ౺๚๔ၙڊ" :floats-pop :booleans->code :set-equal? 974879466 :line-intersect? #demo.examples.plane_geometry.definitions.Line{:p1 #demo.examples.plane_geometry.definitions.Point{:x 3.464296875e2, :y 5.47634209e8}, :p2 #demo.examples.plane_geometry.definitions.Point{:x 3.2426953125e2, :y 5.61951041e8}} :strings-generalizeall #demo.examples.plane_geometry.definitions.Circle{:origin #demo.examples.plane_geometry.definitions.Point{:x 4.72265625e1, :y 7.54056576e8}, :edgepoint #demo.examples.plane_geometry.definitions.Point{:x 3.4683203125e2, :y 8.1780939e8}} :vector-shatter #demo.examples.plane_geometry.definitions.Line{:p1 #demo.examples.plane_geometry.definitions.Point{:x 8.4296875e1, :y 6.72638234e8}, :p2 #demo.examples.plane_geometry.definitions.Point{:x 4.8609375e1, :y 6.297446e6}} #demo.examples.plane_geometry.definitions.Line{:p1 #demo.examples.plane_geometry.definitions.Point{:x 2.1125390625e2, :y 9.69746222e8}, :p2 #demo.examples.plane_geometry.definitions.Point{:x 1.0140625e2, :y 7.5705451e7}} :chars-reverse false \ᇎ :floats-cutflip :string->float #demo.examples.plane_geometry.definitions.Circle{:origin #demo.examples.plane_geometry.definitions.Point{:x 3.048828125e2, :y 5.15763204e8}, :edgepoint #demo.examples.plane_geometry.definitions.Point{:x 1.32765625e2, :y 5.96831076e8}}]
  :bindings {:input!1 :floats-replace, :input!2 "าǴ݉ੴ๩ߝ఺ŷວʙટชᇙΠȖ়", :input!3 [\Ҡ \ \ௗ \ݲ \ྪ \ᄋ], :input!4 :floats-emptyitem?, :input!5 :string-replacefirstchar, :input!6 :boolean-return, :input!7 :char->string, :input!8 :set-subset?, :input!9 [4191 1087 1982 2405 917 2469 4206 1037 1141]}

  }
])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

