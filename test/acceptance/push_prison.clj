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
  ;; timed out 
    :program [:floats-length :chars-yankdup ["ဤྭণੰ̇Ȗ"] #{228.796875 :booleans-set "఍ˏǹய઼ૣላ" #{:vector-byexample :generator-counter #{[4813 148 3333] "઻ُċࣣฤΟʝϋLƐ" [\ൽ \৙ \߶ \਌ \ያ \൉ \ጯ \ۇ \Έ] '(\؉ :point-flush [\Ѷ \ഏ \ࡺ \ෙ \ఔ \Ϻ \ፐ] :exec-cutflip [0.08984375 0.14453125 0.0546875 0.0625 0.015625 0.02734375 0.109375]) :code-map [0.08984375 0.0625 0.03515625 0.15234375 0.08984375 0.05078125 0.1171875] :string-rest [\˃ \ɬ]} :floats-generalize [true true true false] :booleans-new :vector-save [0.140625 0.03515625 0.04296875 0.109375 0.1015625 0.046875]} :refs-first :generator-again :refs-portion [\ཞ \ྀ \ǀ]} :string-flush 28.53125 \̖ ["ᅽۇĶзࠈϨпИኞ" "঍௟Ѷ੤ቬ႖ા" "ᎌ๢Ӧʦشᅋ࡟" "ܣသ׍૝ీ೓ᆁ௅٘཰൭೭ݨᇍ௏૎ჩፋࣴჀ"] :set->code '(:string-first "༾ज़" :integer-rerunall :refs-dup :booleans-return) :float-ln :string-replacechar :point-pop :refs-do*each :exec-do*count :char-againlater ["ऀ๹ࢤಛ๝ȼ" "Ⴃޭ½̄གᆝಣ೏በ୆ࠤ౗ಧʱыვᎍըӰ" "ͮဿޚպ຅ዼřসለማመ༊ᅨͧዶፉ੶ൄ֩" "ڡ" "ᇽᅳބˏຮຬၛ˽ö̚ಅöৄϻୄ" "ࢊŬ։ዾնǞؕทŘŇྜዻޗņᅛō" "ӆ୔ƹǧ፻ىள͇༜ԯݡ࿄ؗཋƞ୺࢜ઋ"] ["ˆፖཝ.ʩϰޛƮቀ,ԋЈ÷ତૺ" "ᅍზဋ࢛в௸ލҟܹоǮஹ̉" "ྌၹ࡮ื" "ੋۖᄧ੽ԥӳঃጭN٩ݙ܂௛ς" "ྦࡼຓۜऺ࣐༄ढ़ٌ"] [0.04296875 0.12890625 0.03515625 0.03515625 0.06640625 0.14453125 0.078125 0.11328125 0.01171875] \ඟ :char-flipstack :input!7 :refs-rest :string-occurrencesofchar [507] :floats-nth :strings-byexample :float-arccosine [0.0625 0.00390625 0.04296875 0.140625 0.07421875] :ref-rerunall :input!1 :integer-pop "ৗฐŮቒժజ೰఑ັࡸྮ̗౪ඞ஁ಾʈ" :chars-replacefirst [] [false true true true false true true false false] [0.04296875 0.12890625 0.05078125] :circle-stackdepth [0.01171875 0.02734375 0.1171875 0.11328125 0.046875 0.12109375 0.0546875] ["ྉ஌Ǣ" "ಯ¶ኢ" "فظᆗԁࢼਵ୸" "าႬДҊִ݅ምȟϧሼጢራ௾ஊቓɛሧฦಈ" "ۃ঎୭ጞ" "ੁ" "ԋԝƎᆙࡑႹ࿨ϟ" "ነೀ̚"] :point-inside? :input!6 #{:exec-tagwithinteger :chars-echoall '(:booleans-savestack :exec-liftstack :float-subtract :ref-fullquote :print-empty?) 614933839 '(:generator-rotate :integers-take :vector-conj #{"ጂȄ̦̇ᄄīැ༥სاݤ॒౭਴" :exec->string #{31.86328125 :ref-fullquote :booleans-againlater :float-arccosine :integer-flush false :floats-portion \ౙ} :integer-dec [true true false false false true false false] [0.0703125 0.00390625] :floats-yank :floats-cutstack} :strings-contains?) :boolean-shove 385.95703125 :integers-emptyitem?} :point-empty? :string-notequal? ["ޯ" "ි" "࠱ധ৫ಾທߛěۉ৲ჾ৲" "ઇ؆໳ဠຣॢসƚЗǾ"] :chars-conj :float-print [] [\Ϗ \ท \ቮ \գ \঵] :booleans-contains? :floats-storestack :refs-echoall :integer-liftstack :chars-later :vector-refilter "ᆋሟီᇰቓͣྶΙ" :ref-empty? :input!1 #{141.4296875 :circle-empty? :circle-surrounds? :vector-flipstack :char-store '((:string-sampler :floats-cycler "ትнబ੦Αཹ৭èፆౌनඦ͒ዊȱvߴϸ" :generator-yankdup false) [0.01953125 0.01171875 0.04296875 0.05078125 0.08203125] :input!5 :point->code :ref-echoall) [true false false true true false] :float->integer} :point-dup :boolean-flipstack 822258475 :char-tagwithinteger :vector-echo '(:integers-stackdepth :floats-return-pop :chars-set :exec-y :generator-rerunall) :line-stackdepth 918259206 :integer-add :integers-build false [0.0390625 0.00390625 0.14453125 0.0078125 0.02734375 0.05859375 0.12109375 0.01171875 0.0078125] ["ࠎ֚&व؏ชᇸبफ़஘ࣅᇬጷƣؖᄘᇳྙଜ" "ࢼܠĒჁˆ࿒৯նี" "ا" "ትࡼભʳंᆆဋ೛࢛Կہཊಷ౫"] ["вހʓളᇓ൙ĉ፰ǎϬ೽ӪѽᆆҌ" "Şڋ৤ղ౓५แፌᅚƨ" "༖ԣĆഒූǰयਂ؟̙âǏˤཱp" "෬ၕქǡ6਴֊ᇌဿ଄਩ৰm" "ː௅ບޘ༗Ìဗ޲" "ාՒሤሻࢌɬፂ૨ቘ̐ᆭ" "୬ႄᅓ௔եࢶࠐྷೕथ࢐Ūޜۣ" "ฒ৹ৣփట೧" "ዥٌȜˢԃ௽ߍ໰਴અˢĺඅńኃ፥ۦ؄"] :generator-rerunall :chars-byexample [0.07421875 0.0546875 0.12109375 0.015625] :input!6 :log-empty? '(:exec-echo [true false false false false true] [0.1171875 0.0390625 0.0859375 0.01171875 0.1328125 0.01171875 0.0703125] :strings-echoall :integer-save) [\࿓ \ᆶ \ʌ \੎] 85.20703125 :floats-replacefirst :circle-store :error-empty? :input!8 ["ఃڦሟ౳ซ઴ทఘ" "Йቃ" "ʎǡঃদయᆳୋ࡬ᇴќብȽ๬ࡂጙ" "نကທਲ਼ᇐઆใৢቐǯคคს࡬ψྊۘʺǚú" "Ⱦᅡబ଄҃৅ࢄ൹ດෳ֏Ͻވʕ౟ƁᅮɄԯ" "è°ཁ" "ᄬɵ֒ʧ਎ไၽǀ౰ƌࠒ፴" "ໟౢၽፉౢ̱ƓጕႄҝࣁጢݠҎಟ˂ʗ" "ࣺ჈"] :booleans-return :integers-last :integers-first [false true false true true false false true true] #{'([0.14453125 0.02734375 0.0 0.0859375 0.0 0.015625 0.09375] [\૏ \Ɏ \੓ \Ȭ \ஃ] \ԩ #{:chars-shove :string-flush :float->asciichar [0.078125 0.02734375] :refs-rotate false :strings-new :set-echoall} :booleans-rerunall) :booleans-notequal? true :exec-stackdepth :string-storestack :floats-rerunall [0.0234375 0.00390625] :integers-flush} :boolean-flush "ſზȨŒ଩༾ၐϥอ" :char->float :booleans-replacefirst :integer-later :booleans-flipstack [0.15234375 0.125 0.1171875 0.078125 0.05859375 0.08984375 0.10546875] :booleans-flush :floats-byexample :refs-yankdup :strings-contains? ["અ੕მପćΤŒ਒" "ФՑϓ৲໛ሩஂጃതᄁܶኙ࿣ᇌठΗำ፟" "ఫӘൢུԱ፤ᄄڲࢥፁௗࢄ"] \_ :refs-conj :floats-remove :generator-stepper :strings-sampler '(445839074 :refs-occurrencesof :string-cycler [\ሕ \Q \ృ \໱ \ఠ \ಘ \Ⴂ \७ \்] (:integer-sign 248900556 #{67.55078125 [0.12890625 0.06640625 0.12890625 0.10546875 0.1015625] [true true false false false false true true false] :set-flush :integer-few false [0.15234375] \ʺ} :integer-rotate [0.125])) :float-mod '(:floats-set :vector-take :point-oncircle? :integers-notequal? :circle-dup) :vector-occurrencesof '(\ৱ :vector-swap :vector-replacefirst :input!1 :code-map) :circle-later false ["ഫᆵกчΆ༉:ݺ{໮ͺൡᅸԲฅબ޳Зౝ" "ئ" "๙ˍ໦༦Űౌ" "ݵ̨ࠪؑ቞ėஅ༇ඬ"] [] :strings-comprehension :line-flush [\ቭ \ี \ғ] :vector-later :chars-storestack [\प \હ] \ϭ :string-storestack :exec-when ["\\ϔࣹതϲբ਻ᅃڿၛটࢩݐh" "۲էᆽЗଯዿ" "೅ྶϏɊѺЉ໧ണά]џΏ˸ͥ" "൴Ӭᄏჴ༏ʧரᅨۣ܊ʢಒ߫" "Ʉ" "˅ຯ೺άƞÇड़඙Ұէೝཱჲಢǃǔ" "ቝ౽๏ጶࢇ¼ણࡱ"] [\્ \ኋ \ຆ] :char-cutstack :boolean-tagwithinteger " ڥ" :refs-contains? :integer-inc [0.09375 0.10546875 0.05859375 0.0390625 0.03515625] :string>? #{:input!10 \ਬ :booleans-notequal? :line-swap '(:integers-emptyitem? :exec-store :float-abs [false false true false false true false] "ՙ") :ref-stackdepth [510] :string-substring} :input!9 :integer-againlater 191136846 [true true] 725985855 :strings-cutflip :strings-savestack 832968566 [0.01953125 0.0546875] :integer-abs :set-comprehension 314.328125 406539936 :code-size :ref-echo :line-pop :ref-dump :input!1 :vector-liftstack :strings-shove :float-print #{:string-later 175.38671875 :refs-rest :vector-shatter :strings-return [0.0078125 0.03515625 0.12890625 0.0078125 0.1328125 0.02734375] [1409 4079 1241 2014 1463 2604 683 2123] [3022 1165 4568 1896]} :integers-contains? :refs-stackdepth :floats-echoall [] :boolean-return :integers-cutflip :chars-flush [false true] :input!8 false [0.078125] :chars-notequal? :booleans-tagwithfloat :integers-rest :code-do*times :circle-againlater false :booleans-conj :circle-shove :float-subtract "ற།ൎ࢑ेᇈʴۨמৈ¡" 151.921875 64351713 '([4131 1998 4733 72 159 1241 3318 4197 381] "۹ႆւիΦ࿱̗༕ᅲ=଀܌చ೰ኽџੰ" :floats-new :input!10 :set-comprehension) 280.078125 \ᇵ :input!1 :integers-empty? :floats-take :ref-empty? :float-sqrt :print-stackdepth :float-E :input!10 :integers-shove [\Ʊ \ܫ] :generator-cutstack :integers-remove :input!8 :input!8 :ref-flipstack]
  :bindings {:input!2 958856226, :input!9 false, :input!3 :float-sqrt, :input!10 :vector-refilter, :input!1 :booleans-flipstack, :input!8 \, :input!4 :integer-storestack, :input!7 ["ҍ໭፩ѸၳࠈჁ" "ေఽ໤௡ɒ" "ইݼጃҀ" "Gౕઔনිᅚದ჋ॊண" "ଶ]ߘఇԙሰሴߢĥᆶӌບOᇮፅǈ" "λÜᅎᇼ̔ഴኔ೘मᎉ" "िஐढơଌࢦࢨ໛ܤŅּ±ᆗ،ࣄ"], :input!5 203.30859375, :input!6 ["ʟҎᅃҨܴ།৽চ́ሿ" "ᇩ༚ࢤอ፼ೆѓ೦ੱ৑˽಴ଙ"]}



  }
])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

