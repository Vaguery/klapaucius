(ns acceptance.push-prison
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.type.core :as types]
            [push.util.stack-manipulation :as u]
            [push.util.code-wrangling :as fix]
            [clojure.string :as s])
  (:use midje.sweet)
  (:use [push.interpreter.core])
  (:use [push.interpreter.templates.one-with-everything])
  )


(defn overloaded-interpreter
  [prisoner]
  (-> (make-everything-interpreter :config {:step-limit 20000 :lenient true :max-collection-size 138072}
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
                            "\n items on :scalars " (u/get-stack s :scalars)
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

    '[false :scalar-bigdec? false "9?VCVæÛ%&R×ß¸" \å :refs-againlater #{150.3671875 :boolean-yankdup (999920925M :refs-replacefirst :chars-store 101/36 4.25) 950470192M :booleans-liftstack ["BÜÛÖt¦CÒkã·°" "<FÛY" "|]}Æ(Ï" "R²" "äUT2" "ãEÞMæçHq´S$ºÐL]¿³"] :booleans-conj-set :refs-shove} :input!2 :boolean-store :complexes-length #push.type.definitions.complex.Complex{:re 50256273, :im 27.68359375} 603945569M false (:input!7 :chars-build 92.13671875 :strings-rest :intervals-flipstack) :code-size 759643396M :code-size 642144788M "¦JDÈ" :input!8 [true true true false] [true true false true true false true true] \L :chars-return-pop 110340737 [1621 1607 3948 4877 4845] :generator-savestack (:input!5 :chars-in-set? :intervals-cyclevector :booleans-generalize :input!1) :vector-rest :tagspace-count :code-yank [0.07421875 0.0546875 0.12109375 0.01171875 0.09765625 0.01953125 0.11328125 0.03515625] :complex-echoall 333.1953125 :scalars-flipstack :code-insert :booleans-return true [false true true false] ("O:£N#H\\&@­" 454360265 [0.00390625] "½~Jfj¯y;=" ["B»àm{ÕwÓ¬¾»W.À" "Íj²Q[¦QË­NÛAçÏ&°ÛÓ" "¯¯ãÐ©Ä4" "hi\"Jv¸j4WØ)0Ç[" "O¿*ÒE" "yk#G"]) [\@ \L \; \´ \ \q \6] #{:complex-dup [2940] [0.04296875 0.015625 0.10546875] :char-liftstack :refs-cutflip \V :set-save :boolean-arity2} #{\A #push.type.definitions.complex.Complex{:re 870291221, :im 125.90234375} :complex-make 25/168 false :string-containschar? :complex-rerunall 140287203M} ["c" "o¶ÇU­©V¸\\º]R­C´V9ÕU¾" "BunEÔL`Èq" "E+c½Tªxv%NY¢" "¦¤Xc{á[4ÆâSÎh¹" "·+ÑzÆBÄi" "¡uqa =%Ay' Â)NnJ"] :complex-yankdup :strings-butlast [3738 1845 3155] :strings-store :vector-sampler :scalars-set 495361340 [\{ \j \] #push.type.definitions.complex.Complex{:re 958591482, :im 224.96484375} :chars->code :ref-cutstack :booleans-vsplit :refs-first #{#{(:scalarsign->boolean :strings-concat 68.62890625 [false true false true true false true true true] :scalars-last) 3/11 [] \s :complexes-comprehension 651230699M #{:scalars-take (:tagspace-new ["[T¾½" "Zß¨pp£|Þ" "¥Ç¿i Û]B»QG¤]Jx" "¥m6ÐF¥ÖàÉSÙ" "Ë«E²Jt±E{" "7Ü" "u&w¶Ã©fH" "ÜÒ¡"] :code-swap [0.08984375 0.03125 0.01953125 0.14453125 0.0546875] :char-store) :booleans-sampler 513225581M "Y¡¸®l~|½>p!" #{239.5703125 #{["¼/×¨ÍØ"] :generator-flipstack #push.type.definitions.complex.Complex{:re 666282387, :im 27.0078125} #push.type.definitions.interval.Interval{:min 13/71, :max 388.71875, :min-open? true, :max-open? false} :refs-reverse :code-liftstack (:intervals-emptyitem? [0.09765625 0.13671875 0.08203125 0.0078125 0.12109375] 292430260M [false false false false true false] [false true]) :complexes-rest} :string-spacey? :tagspace-return \Ø #push.type.definitions.complex.Complex{:re 407058073, :im 183.44921875} :tagspace->code "ã"} :complex-againlater :intervals-vsplit} 128614331} #push.type.definitions.complex.Complex{:re 325476183, :im 342.57421875} :scalar-echo :vector-replacefirst 439186253M :ref-known? :set-save :code-echoall} :ref-in-set? #push.type.definitions.interval.Interval{:min 4/13, :max 233.90234375, :min-open? true, :max-open? true} \- 73.86328125 "Ú/*3" :scalar-conj-set 118/19 \ß :strings->set 165714584M :boolean-pop 23 true 17104560 :input!1 #{[1907 1728 299 753 3012 948 578 1857] :ref-equal? :interval-scale [] 862834783 :vector-yank :input!4 false} 23/9 ["Ë,]rfy¥qãB·È"] :code-intoset true :exec-while :string-yankdup (:complexes-print true 14/3 313.625 357.73828125) :intervals-savestack :scalar-E :scalar-ceiling :input!8 :boolean-later \k :complex->set #{\l [\ \Û \C \ \ \W \m \Ê] #push.type.definitions.complex.Complex{:re 484591306, :im 271.83203125} [\× \m \1 \ \] :string-cycler :complexes-echoall :complexes-do*each :input!6} ["^f©HÂ)UsÕT×ZsuàÂ#?Z" "¾¦¹" "q3¤´?Õj"] :interval-union :boolean->string #{\á #{[false false true false false false false] :scalars-shove [2798 2687 4227 875 4217 3955 804 2144] 110/103 #{226.89453125 ["X0" "Å6b]¨U§" "pWU1»¶V~Gx& Q.fY¡" "¿· x"] 23/148 #{[2102 2666 3151 3461 3113 2289] :snapshot-yankdup ["9»¿á£IoÐË<[Ãx" "vkzÚÓ.seVq´ :"] [] #push.type.definitions.complex.Complex{:re 314308977, :im 18.68359375} [\v] :refs-shatter 157/39} 103888715 :scalar-reciprocal :input!7 :scalar-cutflip} :char-yank 328827502 \|} #{:string≤? #{"!9]I®rrn`6'4!" :strings-build 804014716M :boolean-stackdepth 106/125 \W (([\ \b \½ \n] [3280 2640 2392 4128 1298 2218 3545 251] \ [] :code-savestack) [\; \´ \ \1 \X \C] :interval->code [\ \ \Ò \@ \¨ \Ý \! \/ \±] "Ý") [false]} #push.type.definitions.complex.Complex{:re 501903630, :im 125.3515625} :strings-echo \ç :refs-stackdepth ([true true true true true false true false] 1/8 true :vector-conj-set :vector-shove) :scalar-yankdup} #push.type.definitions.complex.Complex{:re 664965251, :im 175.88671875} :string-againlater 586646959M #push.type.definitions.complex.Complex{:re 904471213, :im 281.61328125} [\ \a \U \Ê \¿ \Å \X \{ \k]} [\ä \R \J \Ð \0] :booleans-conj-set :booleans-first [false true true false true false false false] [\Î \ \# \Ç \v \¦ \¸ \l \U] [1237 999 4490 4286 3538 224] :string-cutstack 95/149 :ref-dump :booleans->tagspace :scalar-power :scalars-dup :interval-storestack #push.type.definitions.complex.Complex{:re 94615855, :im 256.8203125} :input!3 :string-savestack 526851075M [false true] :scalars-set :chars-notequal? :refs-later ["s;¤ç±j±Ü·à" "Y¯~Xåc==Þ¾" "àmÇÝz¦?bn&<Þ"] [3244 3162 1641 3437] :scalar-cosine "40GIÍgÃkåá" :chars-storestack #push.type.definitions.interval.Interval{:min 68/185, :max 260.36328125, :min-open? false, :max-open? false} :complexes-last 68.0859375 (false [0.13671875 0.13671875 0.1015625 0.1171875] :booleans-pop [\k \L \C] [true true true true true true false true]) :chars-butlast #push.type.definitions.interval.Interval{:min 123/137, :max 267.0703125, :min-open? false, :max-open? true} :strings-items #push.type.definitions.complex.Complex{:re 717261691, :im 379.0} :exec-storestack [3469 4694] [2612 1418 151] :interval-max :interval-pop :booleans-rotate :strings-tagstack [] #push.type.definitions.interval.Interval{:min 53/178, :max 289.24609375, :min-open? true, :max-open? true} :complexes-return :set-againlater :chars-reverse :string->code "E3(" #{:generator-return-pop 304748474 #push.type.definitions.complex.Complex{:re 461130447, :im 199.74609375} #push.type.definitions.interval.Interval{:min 62/65, :max 57.6171875, :min-open? true, :max-open? false} :complexes->code :set-intersection :intervals-cutflip :scalar-tag} #push.type.definitions.complex.Complex{:re 286433514, :im 181.62890625} (:scalars-replacefirst ([0.03125 0.1484375 0.0625 0.0390625 0.03515625] [] #push.type.definitions.complex.Complex{:re 332889724, :im 104.6875} true :booleans-in-set?) :interval-return #push.type.definitions.complex.Complex{:re 873043991, :im 58.99609375} 55/149) :code-rest [4003 504 2761 2256 2416 1079 1694] 873829510 :input!1 #{289.3359375 :code-pop [] 386.22265625 :input!4 \Õ :vector-cutstack #push.type.definitions.interval.Interval{:min 59/30, :max 29.171875, :min-open? true, :max-open? false}} :string>? :set-notequal? " Ü²fÑ;Ô]Âå" [4892 3151 831 1705 1339 4446] 133/4 (:complexes-cutflip :chars->code :input!2 #push.type.definitions.interval.Interval{:min 49/20, :max 273.43359375, :min-open? false, :max-open? false} :scalars-replace) (:booleans-cutflip #push.type.definitions.interval.Interval{:min 11/4, :max 25.109375, :min-open? true, :max-open? true} #{:integer-uniform (:complex-store :vector-refilter :char-tag #push.type.definitions.complex.Complex{:re 940223416, :im 336.00390625} (:set-comprehension 340.54296875 :snapshot-againlater :interval-dup [true])) ["4P;S#?ÝÃPºÀßj¬Õ~h" "{*·ÑL¼®ÄS>@¹k" "q¾:µzK¨ß-(F#H¹Ë" "OEÏç­» IYnÄY,Ãqµ" "­P)|ÏÑÇDiÚå" "BÊ× w®Ob¯¯XáJÙ" "Íæ£S¶°²>" "­._ä«å" "Ò!{°¶¬"] \) :boolean-intoset #{:generator-totalistic3 234.953125 [3083 716 1162 4582 2068 845 613 428 4062] "â^" 16/25 [2996 3421] :complex-tag 4/139} [\ \" \¬ \J \¼ \ \¡ \Z \ª] \} 274399381M :complexes-fillvector) [false false true false true] :scalars-take :intervals-conj :vector-replacefirst true true :complex-echo 335.7890625 :scalar-stackdepth ["¼ .ÛÊ§LÚç¼­¹" "*n" "M;;}¨qÝtÍ&GCÔ0O\"" "9" "Á$V-" "¡" "vA:\"·F¨" "½w8âË,QÓÁ7×µ"] :refs-first "#U9=u" 284.83984375 :booleans-vfilter :strings-vfilter :code-tag :tagspace-cutoff false :exec-save :input!5 ["Ö$1T®·²1A¢Fa²u ¥"] :exec-in-set? 647835534M (:tagspace-stackdepth #push.type.definitions.complex.Complex{:re 333455984, :im 104.0078125} #push.type.definitions.complex.Complex{:re 934970989, :im 38.953125} [0.0234375 0.109375 0.01953125 0.05078125 0.01171875 0.015625 0.0625 0.15234375 0.03515625] :input!7) :scalars-yank :code-return #push.type.definitions.interval.Interval{:min 151/115, :max 81.98828125, :min-open? true, :max-open? true} 239.55859375 [true true] :scalars-empty? [false false false false true false] :interval-print 566073321M 145.41796875 :code-insert #push.type.definitions.interval.Interval{:min 66/101, :max 344.265625, :min-open? false, :max-open? false} #{3.44140625 :complexes-shove 179092962 :interval-parts 21/37 :code-do*count false #push.type.definitions.complex.Complex{:re 707751575, :im 260.97265625}} [2468 3079 2711 3383 2052 1327 66 3492 3734] :refs-later [2222] :vector-cycler :input!2 :strings-notequal? :refs-empty?]



    :bindings 

    '{:input!1 (384878736), :input!2 (:code-drop), :input!3 ([0.09375 0.140625 0.1328125]), :input!4 (:refs-stackdepth), :input!5 ([true false true false false false false false]), :input!6 (:exec-yankdup), :input!7 (:intervals-conj-set), :input!8 ([0.00390625 0.078125 0.109375 0.13671875 0.0234375]), :input!9 (["¯z©½W*iË¤xYÄ0'+c" "Ûáq³ gÆB¥" "ÆÁ?P­JÈÇ" "GJ{Ýt?ná%~à" "QOÕ>SZßÛ" "^Gg¿|Û'ÃFÅG¸l«?:Ò" "yV&}´)IÊm]oÈ" "%Âcy@" "k7]·°"])}


}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

