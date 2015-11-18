(ns acceptance.push-prison
  (:use midje.sweet)
  (:require [push.instructions.dsl :as dsl])
  (:require [push.instructions.core :as instr])
  (:require [push.types.core :as types])
  (:require [push.util.stack-manipulation :as u])
  (:require [clojure.string :as s])
  (:use [push.interpreter.core])
  )

(defn check-on-prisoner
  [prisoner]
  (let [interpreter (reset-interpreter 
                      (make-classic-interpreter :config {:step-limit 50000}
                        :inputs (:inputs prisoner)
                        :program (:program prisoner)))]
    (try
      (do
        (println (str "\n\nrunning:" (pr-str (:program interpreter)) "\nwith inputs: " (pr-str (:inputs interpreter))))
        (loop [s interpreter]
          (if (is-done? s)
            (println "DONE")
            (recur (do 
              (println (str (pr-str (u/peek-at-stack s :log))
                            " >> " (class (:item (u/peek-at-stack s :log)))))
              (step s))))))
      (catch Exception e (do 
                            (println 
                              (str "caught exception: " 
                                    (.getMessage e)
                                     " running "
                                     (pr-str (:program interpreter)) "\n"
                                     (pr-str (:inputs interpreter))))
                            (throw (Exception. (.getMessage e))))))))

(def prisoners
  [
    {:program [:float-cosine \޼ :boolean-stackdepth '(:integer-empty? :string-contains? :boolean-rotate 282191213 159591566) :input!9 \ʹ :string-frominteger false \ᅩ "ๅѻڠဉഊͼº঻༪જ΍ʐɚຢ฿ྍ" '(976731884 :input!5 620338337 :float≤? 259.13671875) 418736571 :input!9 \୘ :input!9 :char-rotate :input!9 317.7578125 300.578125 358.90625 :input!1 413891888 '(:input!4 227.00390625 true :input!10 (:string-substring \ݽ (:string-fromboolean :char-notequal? :input!1 :boolean-not (\ɂ true :exec-s :float-tangent (57957029 :code-drop :input!10 :boolean-equal? (:exec-swap 86.83203125 \î :boolean-and (true (108091590 932801264 :exec-do*while :float-stackdepth :input!6) "ཿԏሹΆඌ༴వጰஎንၳශࡍ૕ᇧ઼ౡሎඩ" :input!6 (:string-shatter :string-fromfloat :integer≤? :code-swap :code-fromboolean)))))) 126.046875 "ڐ૯ᅈࣂࠁ\"єk")) :code-flush :code-first :input!8 \ݪ :string-occurrencesofchar 466753945 '(:input!8 true :input!4 "ԧೖޛŶ" 164033808) :string-occurrencesofchar 906438887 "ϒҊáٳۆፖᇅƏોᄼፊ࢈ӎ" 262.0390625 :exec-rotate "໕༪ࡑá" 280.05859375 :code-insert 354.9296875 :string-yankdup "࠭ᄰടਆടሮ๸ዥ࡝ࠕઔኞਡ֙஘" 254450119 "ڞጎෟಓ" \ਖ :exec-while "ਗ਼ਾ಼ରඒ" "ഀཱི̃ጉ౭Ճਜಢჷ፛ၜᄼჇऄřྥѐݎ" 231.6640625 :string-fromboolean :code-do*count :string-shatter :float-dec :char>? true \ᇬ 353.9140625 '("ഁϱ຅ಭڸඌ`ঋ௦" :input!2 :string-replacefirstchar (:float-flush 382.23828125 "೻̈זִฑ੯๚ซǖĵ༇ႄą๩శ༧ŞRᇏ" "ზܤႣፙ୚ʅૈెďᇲኜဩ" :input!5) 912365518) :integer-notequal? :input!10 536883161 :input!9 '((:input!3 :char≤? 108.40234375 :input!3 379.94140625) "ᅲ඙ూԝ୸૲ŠഒႧػПઑٙ๨˛থŮ" :boolean-fromfloat false :string≥?) \Ȗ :char-fromfloat true 508244595 :boolean-fromintsign 594994550 :exec-k :string-swap \໼ \ኤ 51834968 :string-shove 378665552 \࣓ :string≤? \ᆴ 299.5078125 :string-solid? :integer-multiply "ङʢఋණಛწ֙ຑɼਰყનĕ" :exec-rotate "ǈ" :boolean-pop 502089255 :char-stackdepth :char-dup false "ךͪͳ૚׹ߪਰ੤ღຨ̷վѰࡈॺìଗ" :string-empty? :input!1 true :input!2 '("ࡔ4༥೮எ࿛۟ჟຎ෋µࠜؒቨ࡞आ˃ຨ" :input!5 (:code-flush :code-extract \๚ 52011980 (230836605 "Ё̎Ӳቧٟࠖk͜Λ" :input!3 true :boolean-dup)) (false \ω (200.41015625 390.0078125 69152495 :char-rotate :input!4) :code-swap \Ğ) :input!7) \ࣸ :float-min 280.86328125 682567452 :input!7 :string-contains? true 592042874 :boolean-equal? :string-pop "ᆵ፼Ĩዐ༶გሲ" "߱ᆩ፡ဢʚޛڼ" :input!5 :code-append \ࡐ \ѻ 188720091 :code-yankdup :exec-equal? '(true 134.6796875 \၌ :string-rotate "ۗੳᆊਙ෬ƝऽÖ෠Øಆᄖ׮") :string-occurrencesofchar \࿄ '(:input!1 :exec-s :float-yankdup :code-empty? :string-fromfloat) :input!8 558553814 :char-asciifromfloat \૷ \ጥ :integer-notequal? '(:code-rest :integer-yankdup :char-dup (225.6171875 (true \ଚ \ሜ \໰ :integer<?) 979394078 :code-extract :char-asciifrominteger) 506574784) 53.390625 :code-frominteger '(:string-first (:string-fromboolean \౥ (:input!9 379410754 ((570831832 false 190.7578125 :integer-fromstring 33.69140625) false false :input!4 false) 265.06640625 (:code-rest :string-nth ("ࡸˣኞፇྲɮೣ" :float-frominteger :string-length false 274339001) :code-nth :input!8)) :code-do*range 103.4921875) :code-do 897782059 (:string-equal? :float-equal? \ശ "਎ٺௐ" :code-dup)) :string-first "ฑၧܳৰˮĜؑೆಖ˭ൈٝ" "۶֭ቩǏ̳" :input!2 43.3203125 "૛໶১፣˗" "ྺPᎎ፲ջᆠ࿕௔ًÝদฝ႟" "ჵƂǣጊϲ၃ئ" :float-empty? 285.92578125 :integer-notequal? '(:code-if \Ԟ :input!3 :input!2 "Җܞ೶˿ܿିߠሉ዆ৢԁࠒڍኌ") :input!10 \ଡ଼ :string≤? :input!10 \Ќ '(:float-shove \ယ 975935508 :boolean-notequal? (\࿇ 976273803 :input!7 :input!3 :integer>?)) '(390131489 true true :float-subtract :input!6) true :char-equal? 337.23828125 3.34375 :char≤? :string-empty? '(474276908 :boolean-fromfloat :integer-fromboolean 682794245 744331280) true :input!9 879068422 \ϴ :code-if :string-replace '(48729313 \ख :char-flush :code-points "ցϬ෣Ǚݬԋ͡ྜ̀௫෴ሳወ෫ዠྶႊव࠰ස") :input!3 '(:float-sine 854764498 111.11328125 false :input!8) \ᇶ :input!6 \ގ :char-dup :input!4 \ต :boolean-notequal? true :exec-do*range \ঁ \༺ :code-noop '(:char-swap :code-quote \ኔ 160.43359375 (:exec-equal? :input!4 \గ :input!1 "໾ு࠷ۚÖĩ࣒ড়ׇժʒߧ")) :input!3 982933552 :boolean-rotate "֡႟ዳϊʩႌ্" true :integer-shove '(224552249 :char-equal? :input!9 true 580727041) 568640978 :code-first '(:float-min 83.01953125 :input!2 "ᅊ" :input!4) false :integer≤? \ি "୒ࢨྵ" \২ 678531952 "ࣙືǣٮ̡ፏሙ˼຿ӯᆟࣰፎቸଏϒ̦ގ๤" \။ :float-shove :exec-do*times :input!3 :string-nth]

    :inputs {:input!2 277.40625, :input!9 :float-frominteger, :input!3 '(false \൲ 282.2734375 232513 "ጙ͔৲ءಔથਅɺ೚ຈ଑ൺ෨̉"), :input!10 "วΞǤ༴൭ۛƿȍጣ଍", :input!1 \দ, :input!8 :char-allfromstring, :input!4 :integer-swap, :input!7 :float-shove, :input!5 :exec-rotate, :input!6 false}}

    {:program [:string-fromcode true \ൕ :integer-rotate :input!1 '(73.3359375 :input!6 :char-whitespace? true true) 359773446 "ũđࣕڴӒ" '("ᎃᅝ೭̙༱ȉቂჳÞβe५ۆဒ" true :input!1 :boolean-fromfloat (:input!3 :string-length \ዹ :code-do*times 997098549)) \௠ 807575034 true false 407659334 160.8828125 49.63671875 :input!1 :input!1 '(:input!4 "บ" "ɹဳ" 42.19921875 308.609375) :input!8 885283189 \ఢ :boolean-yankdup :input!5 :float-fromchar 85387900 "ࡸ௛ࣶᇇ־Ďቬᅍ૲" 150.046875 610807620 true \ȹ \࢘ 20.1171875 100.7265625 975190173 :exec-equal? 302.91796875 :input!8 \ġ \ࣅ :string≤? "הބԆ߹ျΝݿʌ௶ईತဃ݉጗ੋ" \୆ :string-conjchar \࠴ 84.7734375 \ה :code-map :input!8 \ܰ 987912332 :input!9 :code-empty? :char-yank true '(:string-spacey? 67.54296875 :integer-signfromboolean :char-lowercase? 82.51171875) 850826865 :char-whitespace? :input!5 :boolean-yank "Ɗట౑ወՃ̏ρज़ݡസධǹੵ႟ลӦ" :code-insert 348.37890625 \ᅫ :integer-divide :float-inc :string-empty? \Ղ \Ȝ "ᇯ΁ѿʤ೽ॶ༳ᆉ಄ғĄͭए6ԝট" false \ǟ true "ޫɳ໅ቦኆصБdߕ਌იѳηጢዒ༲Çଢ଼ת" 648512025 :float-signfromboolean 33.05859375 :float-cosine :code-do*count :char-frominteger :float-fromchar :char-whitespace? \Б :input!3 218.43359375 201165236 \ਗ "ʐÞ߮" :char-stackdepth :input!7 '((\შ false (:char-frominteger "࠾उŚքĢჿ௻ொป३௳ภԤܨǊ༲඲߃ࣛ" :string<? :integer-empty? :input!7) (75058234 :integer-dup :string-concat "Dʼਰ೦ၹ໘ᅷલࡡద" :input!6) :input!3) 840723436 \໖ :input!4 :integer-min) :char-min '(:boolean-or true :input!3 true 183169453) :string-shove "Ӊଷැ܁෮ˤଅ؁૾มఽĴಠส\nሄĕղ඘ᄽ" '((:string-solid? (\Λ :boolean-flush :string-take "ঈӌמǴĒࣃ" \੟) true (true "͢ݭ෠ƷՒΝన" \ൡ false \Β) (true :input!6 304766302 (:input!1 :float-swap \ഁ :input!8 \঍) :input!6)) false \௥ :char-asciifrominteger :code-flush) 337.63671875 true 591602632 :integer-sign "ඁሚྞϽย൰̘ôྒྷߢጥ๻ልރ՞ැኄ൓" false true 872819601 :input!8 :integer-fromboolean :string-frominteger 196.6484375 :float<? 311.90234375 '(:boolean-not :code-notequal? \൪ (964791291 :char-uppercase? :code-nth 0.28515625 237.60546875) :input!1) :char-allfromstring '(919360138 :code-points :code-subst :string-reverse true) 114723043 :code-container true 646830982 "שÏప۫ே୞۴ʻ" "ᎈࢶሎလ੮هȘຯ႒೐ฮ" 374.05859375 false false :exec-shove 556285811 187.42578125 788181234 :exec-equal? \ಫ \ख :exec-yank true true \Ң "ƊߢѦᆦ̃ƺᆛྀުথࠌ቟ٔ" false '(true 159.55078125 :input!4 \ŧ :string-concat) :input!3 651560591 '((:char-yank 264.203125 139.80859375 "ᅣ੾ዒаȌ༤ཾࣵ" 802638436) 290.3203125 163.4765625 :input!7 :char-equal?) "ॕ֫ʝlƃჯϻ" :input!1 :exec-k 936832850 :input!5 '(11.546875 :code-insert (:string-nth false false :integer-fromstring :code-drop) false \ʎ) :boolean-swap false '(:float-sign (:code-fromfloat 37.578125 :code-extract :float-multiply (\ˆ :input!8 :code-if (15.46875 :float-fromstring :input!2 116.234375 \೪) :code-flush)) 34.60546875 :float-max 708025085) :float-cosine :float-equal? 299.31640625 247.8359375 :code-null? \ግ false 127416306 :code-if true false :string-fromboolean :float-frominteger "ౡƷ̺͠پሞٝ͞Ǣۍן" "ߥಚ঵" :string-contains? :integer≥? :code-wrap "ࡓঘُFʻਅ" "źኯ" '(159.640625 \஢ true :exec-y 185.5625) 270.95703125 '(\܆ :code-cons 166.40234375 "͕িԣ" :integer-equal?) 246.5078125 :char<? \Į 372.80859375 79155120 false "˨ຠቸᆄພܟ֕ᆵ੬௧໓ᅔ೛࿆ጜৢઠԔࣿٳ" :input!5 :input!1 450015504 false 277.2890625 :input!7 :input!7 :boolean-and true \ɝ :string>? \Ӥ 457297560 true "॥ѨǠȑ߉̳ᄽܜ" "ֿèһᄱ؂ÊĻૈѢذuࡓྡྷఈᆽśบ" :code-rest :string≥? :string-fromboolean "ΰЈ઼·ງōϊƙᄦ" "ၩೣयරใַཛྷቢঢୖੂ౤ࡁዠ఍" :input!4]
  :inputs {:input!1 "ݩཀྵ႟", :input!2 434977606, :input!3 false, :input!4 '(387.4921875 86.78125 true 144.74609375 "ڎैߏۦ"), :input!5 :code-fromstring, :input!6 '(\೒ "߂਒ईհᆂ^ູñஔƁቬϮ࿣ࣔቶ፰" :integer-flush 202.5078125), :input!7 \, :input!8 :boolean-fromfloat, :input!9 :boolean-shove}
}


;; '(() (() ((()) \ଆ) ()))'
{
  :program [:float-inc 567504269 \߇ "ُ஘ఖଌᇧᄣ͆໩ࢥསp಼ૡዉ༪ໞࣟʯןϒ" \ݡ true "๙" false '(false false :code-do*count "नȒڗડள؍ܮવጤߪ" :float-sine) :integer-pop :input!4 :float-min :integer-rotate "२]๷ཋ̢೉༰፬" 27.5234375 '(292.2421875 :code-append (\ଃ "ॸĥທ֓ےĠ୽ᅐࣛઅುౝܣଉද" false 96098581 972318354) "ऩ४" 266.53515625) '(false \ࠏ :code-if (true :code-fromchar 328.86328125 true :input!4) ("ဘȄߎგ" 631952271 \բ false false)) :string-substring :boolean-stackdepth 356.89453125 :input!7 "क़ࡆേኗໟ༕ໜݥҭຌไӎ" :input!8 \ଆ true :integer-pop :input!7 :string-emptystring? 893501391 \ၐ :exec-dup :input!7 235.14453125 true 60.0859375 "ྥ" 154243028 :input!9 :input!2 :input!1 '((:string>? :integer>? :input!6 :float-swap 294.39453125) 950493076 741354085 :input!3 true) :float-yank "ᆰࠤٚ઀ፎư͜)ௌ̅սࣂอْ" 168.63671875 :string≤? "౏ካ˄ȄŰ࿼୑ൻʛ˲ൢቱਜᄤටൻၱ" 15.16015625 :string-conjchar "˶ܙۓކ໺Ϡఃӎqቛ࿾\"֩" true 252744268 true \ל 529347957 :code-cons false '(:input!3 :string-removechar true :code-fromchar :integer-fromfloat) :input!5 :char-uppercase? :float-add :exec-stackdepth :integer-subtract "ิӓ඿๧੅ʄ௮Ĭंሑ" :string-shatter '(("֮ሰŲპܙΆં" "ݢԶၽЛ٪༨ा৐ዄ೘ਧ࠭୹৿ࢤ྅؋" ("߅ႊࢵၳ" \໤ :string-stackdepth :input!2 "Ȏᆋǆۜఏ֦Ĵ႘ᄶဓᇨႃ౳ܦ") 169.3203125 :string≥?) "ԑ੅ޗૅ˙سЕۤŗഒ၎ʃɆ͐" true :code-fromfloat 318.9140625) \ۃ :code-cons 286.85546875 :integer-flush :integer≥? "ࢣ༷ۻማጙ" 222.6015625 :boolean-pop :float-sine :string-setchar \ٗ :input!3 :string-emptystring? '(:code-first :char-digit? 184692249 (false "਽ғ௙ዉ௣෼" :char-flush :exec-if :string-containschar?) :string-setchar) 704656522 166729596 "ছໞԣ೹ᆕۇ಻ᎌޖวேۿΫబĚ" 848371916 :string-replacefirstchar :code-subst :char-uppercase? :exec-shove false 213.0625 :string-butlast \ዐ '(:input!9 (:char-asciifromfloat "࠮ۋ֢ࠠۧ૆Мඊȹྮ̒௞زၶኯͼ༏ܻि" :input!4 :float-rotate 498356367) (65.08203125 :input!4 :string-take :boolean-rotate (:float-stackdepth 137.671875 54479799 :input!2 (:input!6 204.11328125 false true :string-rotate))) \ޥ :input!4) :integer-swap :char-empty? :integer-pop :string-take "੽೟ቢɃዉбʙ໵඙өᇭŋۏӷᅔ" '((:string-emptystring? :integer-empty? :code-map :integer-min 112097913) :string-frominteger "е" "ݺࢬေ೭ೲఒጠᅹŽӠ" 195.03125) 942657138 :string-dup :boolean-frominteger true true '(352.1875 378.82421875 false (886358583 :code-insert true :boolean-xor 234.43359375) :code-list) true 73600798 :input!3 "ɐԽ࠼ിሆݰࠀಳຄ୆ჺ" '("ᄀ௒ማ౺ၤ̊ትლెбይӺዣґҸఊಋζ" :boolean-equal? :input!2 :exec-notequal? 36843464) '(:input!7 :input!5 :input!5 :float-yank true) :code-map :input!2 true :exec-pop true :input!2 39919726 279.42578125 '(:code-list :code-do 169442193 :input!6 :string-take) :input!7 :char-empty? :input!4 762558360 '(:float<? 695925114 (101.50390625 \ଠ (\Џ 196425883 :string-max (\஬ (:input!8 (\ႍ 310080149 :code-fromstring :integer-notequal? :char-flush) (35475424 :char-equal? (867317395 "౾࠾ͺჹޕۼݖஜ༗.੤ෳƻנဟ" :string<? :input!9 false) 330.28125 "ɒهୋนڡᄶୄƥঙŲs") :code-wrap :input!6) :integer-fromboolean 105066400 :code-flush) (:string≤? :char<? 384549711 false 177.7890625)) true :float-dec) (:boolean-fromintsign 805943725 \໣ (:integer-notequal? \ߞ :input!9 true "ĊՐწɴό૨Ȁࠀݰՙ࢔πਰ፜዁ތŗ") 104.046875) 47.609375) 68088252 :exec-do*count \ŏ '(564195931 (:input!9 :integer-sign false :input!3 "ҟಌஂჽሳȕࣳೡೢȉቊ௼Ռͻ৭׿") \ӎ :exec-pop 91.05859375) :code-rotate 140835501 "෾෸࡬Ⴝ" 391541857 "ƫ༟ၩஇໟ੓Ѧώቑa" :integer-add "ษࣟ" 102.62890625 :input!4 :boolean-notequal? :string-emptystring? "ű࿍ඏҺඇ੍ҾǷཻࡒဴܳᅱ" 19793706 919489405 70.1171875 false 84.7890625 706911051 :input!1 235364190 '(:boolean-swap :exec-shove 364.25 "ѱჺӄ඘๩ຈଯއ޽" :integer>?) \ཕ :code-map :input!9 '("ൾᅙ˔҅ಁঀؙЦŠ།ኲఐ" :integer-fromstring :input!6 "ຕࣘ፥མቴป෿ൻ̠ఉǨးधÍ඘ౡ" 17.0546875) :input!4 :float-fromboolean false \ই :float-sign 312.640625 false :string≥? 85433364 :code-do*times "ीᅬ෋" '(:float-inc 421463906 :input!4 \ܺ (438651859 :input!8 :code-noop "ʓӔ,ǮرژŠИཅლ቉඄ǰ" 31772339)) "ૣໜፇඕ١ၗ٭पJ௖ຯర࿡ቴݣԉ๵Λǯϸ" \Q \ส 93.375 342476104 :string-shove \ȕ 150.15234375 '(:char-allfromstring :string-take false \ժ false) :boolean-stackdepth \༔ 608382633 283.0390625 '(:input!4 "௜ƨఏᄮರȴᆨİǜ԰Vገਗ" false false \ܫ) :input!9 140.3359375 \ે :char>? :char-allfromstring :code-yank \Ɵ "ቆข࠸፣ş௙࣏௺ߑۜ༥Ϻ౒ᅇਜ້ᄟʁ" "౭ҶॲૌΞу" 116.546875 384.2734375 false \ቸ :input!3 :input!2 :integer-shove 290.55859375 303.6875 '(832507314 :exec-do*times "ᅅཋڮZณχ๭ሷၸ" :input!6 :integer-divide) 718449718 :integer-shove]
  :inputs {:input!1 '(:string-occurrencesofchar false :float<? true), :input!2 :string-fromfloat, :input!3 "࠳ىᆩ", :input!4 :string-spacey?, :input!5 :exec-dup, :input!6 \ഭ, :input!7 :code-map, :input!8 \વ, :input!9 :code-map}
}

{ :program [:input!4 "ࢧउɩɃሁଏȑᄈČ" false '(74.015625 :exec-dup :integer-rotate (true 368.31640625 false (:string-pop (:boolean-and :code-extract \ԅ \࿐ \ข) \W :code-position :char-flush) :input!3) :char<?) 520286600 false :boolean-notequal? "ȨഞĚࠟĥؿ໢ݤৌ੠ӧܞኇäࢂ௶ॱ3" "ܵు޶ိܤ" \ɝ :string-frominteger :float-dec 139.01953125 :string-flush 74.30078125 :code-append \஻ :input!1 :string-reverse '(\ݓ :string-setchar 407019172 :input!7 :float-shove) \Ľ false :exec-while \߆ 15.87109375 :code-points 474519630 \ග \ѕ \ེ true 973496341 1243686 :exec-s "ჹΘݤ௣ͤᆊလ૬Ǻ" 795729668 :input!6 \ଟ \ಫ \ཧ "ࣅ՟ȷ౟ఆྶ഍ഇҦ" :exec-rotate 304.86328125 :char-uppercase? true false "૜๣༐ࣄ୦ๆሾÚມ̘ᇆร୦" 22.328125 :boolean-rotate :input!3 :input!3 :string-yankdup \Ɋ :code-swap 325.3359375 :input!9 \ᎊ 40.27734375 :boolean-yankdup :string-spacey? :code-do*count "ԛ፭য়ᄵ̣ሇʪ" :char-rotate '((:code-frominteger \࿖ :input!6 "ӵ೛ġ౳ျፉߍ" :code-swap) 656732586 668666758 \ಁ \ፓ) \ϑ :string-replace 145654295 :code-first false 451904153 \ಷ :code-shove "౉լᎋġဲ" 62833308 :string≥? "ሦિɽഥ" :string-splitonspaces :integer-fromstring '("࢝ޘৰҧүฟׁ˅ɵঔɉؗߪҧһ" :code-dup 185.14453125 :string-solid? :boolean-or) 222.3046875 :integer≤? true false '(:string-emptystring? :float≥? 336.38671875 197300236 :input!7) "u୴ቅද" '(:char-allfromstring :float-divide "٥፜܇െჲ௼ህ෫ჟ෍ࡊ" 426826786 563470349) :integer>? :input!3 :exec-stackdepth 228.84375 49.609375 :boolean-frominteger "ýᇛ" '(:code-container :code-cons 127.015625 (:exec-shove :input!7 :float-fromchar 179.6171875 :input!9) 233975465) 738585210 :float-swap :char-yankdup :boolean-equal? 196550931 :string-nth :boolean-shove :input!3 '(\ԃ \ྉ :boolean-and (\ڸ true :exec-rotate \ક "̱ྂध࿹Ӽڽߒౡĵཔԙፇఀښ˓˛") 670396920) :integer-fromfloat :char≤? 782323013 428808573 true :integer-max 383106170 '(:string-dup 189012010 \Ɲ false :code-first) \ᎊ '(:string-fromchar false false :string-shatter :integer<?) :code-member? \Ѳ :exec-empty? 208.12890625 :char-swap '(:boolean-shove \ሂ :char-whitespace? (\ሮ \ۚ :char<? :input!2 \˗) \ᄿ) 114957772 "ഌ૏ᆨოՍ" :string-contains? :string-pop '(9.76171875 (:input!3 \Ǜ :code-contains? (true ("ࣕ࿇ŜĚ?כϔ஡" :exec-string-iterate :code-notequal? :string-equal? 994036748) true :exec-empty? 631330803) 422523687) :exec-shove ("ɹ̄ͣ೒ޕཫፄ" :char-asciifromfloat "ܥክຐǄੀڽ๨൘ݚˌ࡯ࢿท¢֭" :code-yank true) \ੌ) 126.81640625 :char-empty? \౼ :code-position :code-dup '(\຿ :input!9 "Ƙྑࢷ̀ᇛၓŇൣ௘ᇣಡϞჵ̊ۓ୦ᇼᆷ" :string-fromexec :input!5) 830471603 "ุ" :string-pop "ΣБĩፗ̕ԛඎǓ௄фࠄ" "Ñเǫಿưۜგ" '(false ((false 151.1328125 11.76953125 false \ൻ) true :exec-flush :string-splitonspaces true) :code-member? 339.92578125 :string-substring) :code-frominteger \ֲ 166.65625 '(:string-splitonspaces "Ε༖ࠬ๺֮຋" "ೋҐೊ࣌ඓᄙ١ÐษȰ" "ൌŇลयșྻᅥ፯সྤԣ" "ᅒཋ੅ϑᇢᆨ߉ลዋ͐ಭᄧ૴உ") "ঌ౪ሐgь֤ಉڈȆ৖ಣოƷ" :integer-fromfloat :code-empty? 43.10546875 '(587351817 true false (\ƣ :exec-y :string-fromfloat \່ 644365363) 74875075) :code-empty? '(859537792 :integer-max 355934401 false :string-setchar) \Ţ :integer-inc \࿳ 5929651 "೙፲ჳʣၗɨطວ൘ݮ֐ጹณ" :float-subtract false :char-rotate '((:code-null? false 860009383 :boolean-shove :integer-stackdepth) "նܩ" 331.40234375 292.109375 ("ௗ໩ˁWᇢற໅༊࿻ˎഔჂ૤٣" "ी͓ᅂࢠ౾ބӟळࢵ࿹" "̐O૒ናନ኷ಯ" true "юࢧ୪୅")) :char-frominteger false 228.5625 :string-equal? '(225.23046875 :code-append :float-subtract 873509934 :boolean-notequal?) false false :integer-flush :boolean-fromintsign \ᅈ "ॻጘ࿊ǧᇵ" \ೊ 134244408 :string-equal? \І 773986581 :boolean-not :input!1 :integer-empty? :input!6 :char-allfromstring 936727525 :input!8 :input!6 "ठЕ৿ᄕ·ŏO࿍୸˩ࢆჃᎏኍ୵ҙ୰" 906146901 :input!2 false \ᄖ :input!3 552833491 \ถ '(:float-min ((:char-max 137.9140625 283.17578125 \਱ \ں) :float-divide "஥ȑૢϥ࿬ସ኷অ֫ٴྕ࿾ٲዮ՞ቝ" 175.12890625 false) :string-nth :integer-flush :exec-stackdepth) :code-length :boolean-stackdepth "৔ԫࣂවקϛۗߋདࢼ" "ҦҀઢኾࡇࢵŅေૌ༲ொዀݿዟॠጴᇝٖ۶" \ಊ 181.3515625 '(false :integer-fromboolean "Չ" "ʀൣ஼ུ଑Ëԑ؜̊؍ӝຓFೠʜ̌ዱ" :string-emptystring?) false "૳˻˴አ࿶षў॒֛ᇉर͕ৡࢻིৡክ" 960165074 "բɛ'ཎć`Ŭѣӓᅆ"]
  :inputs {:input!1 '(:code-list :integer-rotate :float-yank :boolean-not 316.8125), :input!2 "Ǎʺൾᅹགਤང઴Ж፳ၲભࡢࢫ", :input!3 :code-yank, :input!4 :boolean-yank, :input!5 :integer-swap, :input!6 19.08203125, :input!7 false, :input!8 :char-whitespace?, :input!9 377.0078125}
}
  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))