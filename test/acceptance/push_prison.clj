(ns acceptance.push-prison
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.type.core :as types]
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
                            "\n items on :generator " (count (u/get-stack s :generator))
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

    '[(:boolean->signedfloat [\W \% \5] :set-later [true false true true true false false false false] :string-return) [0.1484375] 496870583M :scalars-return :string-cutstack ["VYI>¦ÝÜ_Õ" "Àwµ9FÒÒE.hw~" "À(¨*åÏÌ¾" "¯d9Ù:B·zw=" "ßaÞ" ")35z-,SwÄÞ"] 162657748 :tagspace-new :ref-swap :generator-swap :booleans-againlater :scalar-modulo #{:generator-totalistic3 :boolean-store #{37.34375 150.6640625 [0.01953125 0.12109375 0.11328125 0.125 0.0546875 0.109375 0.125 0.13671875 0.04296875] (:char-againlater #{:vector-store :strings-cutstack :scalars-store 25/31 "J!ÖWÂI@ØO|" :scalar-swap :vector-conj :booleans-store} 133/34 "k¸Z" :scalar-cosine) :strings-yank :scalar-inc :refs-set :string-rerunall} "WÔN" :input!7 64055714 :booleans-store :chars-contains?} :booleans->code 357589533 :string-conjchar :string-last :scalars-replace :boolean->integer :chars-equal? :boolean-empty? :set-cycler :booleans-nth ["ß¯ÕLÔM¯Ñ hË¸_Ä$Z" "Es¡l½C<PKbz" "ØßaÃ£#¦6¤½§)å*1" "ÈÕãE0°ã" "xÕäyç«¯" "o" "¥ÑæÈLtßÉ" "[ÔÎ:]/\"Ùw`À¤fäÔ" "q3Þ"] 87/32 :strings-concat :code-atom? :input!6 :boolean-print 95/52 :scalar-round :ref-notequal? :char-storestack :strings-replace [1295 197 32 3830] :code-container :generator-return false 8/11 149762196 98495547M :exec-noop \Ë ([0.09765625 0.0546875 0.13671875 0.1171875 0.07421875 0.0 0.12109375] [true false false false] :ref-new :string-equal? :booleans-rest) "X?Ä©T+Î¸Ç&NhËbL" 592375190 :scalar-rerunall 782225776M :scalars-yankdup :scalar-notequal? :code-points :char-cutflip :char-yank (:chars-rotate :strings-dup :chars-conj 225.1328125 :error-stackdepth) :scalars->code ["GU" "à}qpmT×1Ä4Q3QÇQ@Ê¿" "(Î²äoRs~E¼" "Ñ´Ò\"Å_Çj±ÓvÉ"] :chars-tag :strings-liftstack #{386.2890625 :exec-echoall :push-bindings [false true true true true] :vector-comprehension :tagspace-yankdup (["!Á]/W" "E×\\w´¤" ""] 695105777M :tagspace-empty? #{[4783 296 3563 2371 2959 2429 1547] :booleans-set 83/87 :exec-rotate :generator-tag :input!8 :exec-empty? :refs-replacefirst} :boolean-arity2) :exec-do*times} :booleans-stackdepth :vector-reverse [\} \y \u \º \Ã] :scalars-swap :ref-echo :char-dup :vector-concat [true true true true true true true] 779533137M #{:string-replace :input!10 :scalars-first "'A¯" (#{"X£¸zC" :code-do*range :chars-new :ref-storestack [] ["&x[¢EtI©6Ù>·/XrÎ" "Ø"] :chars-empty? [\ \¸ \m \ \À \» \³ \p \Q]} [\^ \% \1 \©] :string-rerunall [0.109375 0.0390625] :string≥?) 261852748M :scalars-dup :tagspace-offset} :generator-later :booleans-last :scalar-few :generator-return :ref-fullquote :string-flush [\ \Ú \ç] \Ó :exec-noop :string-save :string-concat 611658901 :strings-byexample :set-swap 200982631 :input!3 ((([4499 3412 2864 234 1621] :tagspace-empty? :set-superset? 692238961M :float-uniform) [0.125 0.11328125 0.0625 0.0078125 0.1484375 0.07421875] \Í :scalars-dup "YF¾Ò <`{Ãj§Ïh8;O") :input!6 :char-yankdup ["±¼äæ6|®¤¥³<"] :refs-liftstack) :generator-cutstack true 64055239M true :exec-stackdepth :scalar-few :scalars-yankdup :boolean-notequal? :chars-store \` 13142611 661769302 :environment-empty? "bEN×O æO" false :string->chars [] :code-do [2309 4612 2714 764 1650 3717 4364] :refs-againlater :scalars-save :boolean-flush :exec-y (:scalars-do*each 13/31 #{:char-max ["®|-M¬ç#­Õ>¹§" "åHk0¢;2" "ã¼Yz<®4" "Ã¬o"] ([\¤ \å \: \ \ \¾ \Ã \Ã \'] :scalars->code :generator-yankdup #{["_'v" "çb46vrRsÖF¦Ô C¬Â" "QÃÝ(Æp<À@lU×¡" "¥ /Ì+]@" "k£Á¶S´¸´" "ài%%;%Ð" "Ý" "]dDQS¬"] :booleans-set :char-notequal? 157/123 [211 4621 3056 4126 4388] false :strings-last :strings-flush} :exec-do*times) :scalars-rerunall [4760] :float-uniform :chars-byexample :generator-reset} :generator-liftstack :input!3) :chars-storestack :exec-rotate \V 642718856 :chars-dup "G<'/ßKS£¢ßaÃy§¦x" :chars-cutstack \= :exec-print :exec-echo [] ["FuÊPÆq7^" "MS_"] [\+ \. \o \V] :boolean-later :input!6 :string->chars 138234683M :scalars-stackdepth :scalar>? :exec-stackdepth [0.13671875 0.08203125 0.12109375] "8ca¡`ÒÐVE¸" :string-comprehension ["¨Ðex{¾_z¦D®*d®ÃK§" "sVges?" "Ë" "1Þ)oÒsà¡Æ¬,x" "¸mØP/?¡PLÊ{" "ÕÒh*°µXKAx" "1~;ÄÓ¬ÄÎ"] 5/6 :vector-length :refs-build :tagspace-new 48.71875 :strings-return :strings-reverse :string-replace :scalar-equal? [\4] 11/13 :input!9 :set-flush :scalar->string [542 2529 1811 2373 2152 416] :string-swap :scalar-integer? ["]LgZÄ¶^Ð·-¸/0½ÓËá" "m¬µÞÜ[áyn" "S3Ù®#>À2ÑMP" "äsN~2(â(02QBÛ" "P=TãÇÁºÂDP"] "5QR¤h§ÃÁL6å+y=.â" :scalars-generalize true [true false true true false false false] ("ÉÌAÖ" :ref-yank :char-echoall "°ÑRÞYà|?TP¢ºæ" true) :code-noop :ref-print 701715638 16.1796875 :scalar-ceiling :string-emptystring? :char-flipstack :booleans-generalize \c :set-later #{:scalars-print #{:exec-return-pop :scalars-first 112/125 [false false true true] :ref-cutflip :string-first #{\# :refs-byexample :scalars-build #{:char-echo 162.86328125 [4818 273] :tagspace-pop :tagspace-stackdepth false :tagspace-save} [\` \y \r \² \c] :chars-last 935275353 [0.09765625 0.12890625 0.0390625 0.10546875]} :print-empty?} 37/13 \ #{297.1171875 :boolean-2bittable :scalar-abs :boolean-liftstack true (";nu" :input!6 :vector-take false :booleans-cycler) :boolean-savestack :scalar-add} #{:string-min [1816 4438 3180 2750] (:input!8 :generator-savestack 98.5078125 :scalars-notequal? :chars-tag) :set-savestack [true false] :ref-return :booleans-savestack (:refs-notequal? :boolean-savestack :input!3 [\a \ \"] 995832210M)} :strings-indexof \} 46.0234375 :tagspace-max :string-dup 635996139M :environment-begin "×J[¹J" :vector-rotate :code-yank (389.56640625 [0.06640625 0.0625 0.140625 0.08203125 0.01171875 0.12109375 0.08984375 0.140625] :booleans-shatter \8 :booleans-shove) :exec-stackdepth :set-intersection [false] :scalars-new 286.15234375 :boolean-notequal? :set-echoall 519737184 ["¥{#ÆØK(æÆ^A" "£áv$}^" ":O¯"] false ["jØ" "Ímæ®lÖ¥Pç]D.Ê" "aÞ|'O~ËÅ" "V8!d:" "Å³" "«" "xFÖà^?"] :boolean-echo :code-list \' :code-length [false true true false true true true false false] 129/4 :exec-sampler #{[] :boolean-stackdepth :scalar-fractional true :refs-reverse (:code-pop :set-rerunall [\- \å \Å] [true true true true false false] [false true true false true false true false true]) :scalar-equal? (74681627M 6589151M 413159029M #{165.703125 [\¾ \ \ \] :set-yankdup [false true false false false false] :scalars-rest #{[false true true] :scalars-contains? :boolean-2bittable (:code-null? :code-first [3560 3035 778] :boolean-yank :string-contains?) [482 687 3677 4318 1984] :char->integer :set->code :refs-empty?} true :strings-swap} 994477394M)} 56/65 :scalar-cutstack :input!3 :booleans-length]



    :bindings 

    '{:input!2 (676325058M), :input!9 (:strings-length), :input!3 (:set-rerunall), :input!10 (:boolean-return), :input!1 (:generator-counter), :input!8 (:string-store), :input!4 (:vector-replacefirst), :input!7 (735850903), :input!5 (:booleans-later), :input!6 (:scalar-abs)}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

