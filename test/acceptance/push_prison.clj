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
            (println ".")
            (recur (do 
              (println (u/peek-at-stack s :log)) 
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

    :inputs {:input!2 277.40625, :input!9 :float-frominteger, :input!3 '(false \൲ 282.2734375 232513 "ጙ͔৲ءಔથਅɺ೚ຈ଑ൺ෨̉"), :input!10 "วΞǤ༴൭ۛƿȍጣ଍", :input!1 \দ, :input!8 :char-allfromstring, :input!4 :integer-swap, :input!7 :float-shove, :input!5 :exec-rotate, :input!6 false}}])



(check-on-prisoner (first prisoners))