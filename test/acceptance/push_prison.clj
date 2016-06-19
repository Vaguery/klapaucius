(ns acceptance.push-prison
  (:require [push.instructions.dsl :as dsl]
            [push.instructions.core :as instr]
            [push.types.core :as types]
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
                            "\n items on :tagspace " (u/get-stack s :tagspace)
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

    '[:chars-savestack 637140214 :boolean-arity2 :booleans-length true #{(true 50/199 :chars-reverse :strings-last 165/106) :tagspace-equal? ["ç­·Ý¼M" ",µ " "E!LPÊ,pl1æzálç+b" "Z¨A2ÏWä¦S" "?3"] :booleans-butlast [true] [\Î \¾] 97/54 #{:input!9 ["±º7" "¡ßo1p`WÑ;~ÓÁ5¢" "c]Ôb$'æÏ´'" "æªÖ.×E6¬â×" "¤3Å"] :set-savestack :push-nthref :exec-storestack :char-uppercase? :strings-liftstack :refs-replacefirst}} ["­Ã³ÏÐ;¢¤&DÁ" "L­lL2P¶9¾" "!Ø Ô(°iäG"] :vector-byexample :scalar-ln1p ["4ÏBæÞ§E¶²3CX(" "1y" "oµcgK1aÖ"] 696932855 :scalar-stackdepth ["ÑkArç6ß²Ùa¾H»Ô.o4" "$VIEÒZ" "[¤½\"D5µQ×<k;ÕqEA" "h'&lm±+HO,×" "q³£F'YÑ-" "ÜB£B¸Iw" "u/¬~¸"] 137/74 [0.05078125 0.08203125 0.07421875 0.09765625 0.07421875] true :scalars-rotate #{914349951M "o?STÔlT'" :input!2 :booleans-set :string-flush :char-save :code-echo \.} :char-uppercase? :code-do*count ((:vector-cutstack :scalar->code \Ê :chars-cutflip :booleans-emptyitem?) :tagspace-empty? :input!7 ([\¯ \Þ \b \N \w] :chars-remove 76135491 :string-cycler "T") "Ý_j<E") [\ \B \z \Û] 189/88 \[ :ref-new :strings-cutflip :scalar-log10 ["V6&7¿½%!¶v½*" "Ë¡aÃ,0X~TÒcn"] ["3Ü{PÁY¥/ÇÂe3Ó" "É" "Ï§Æ¨×GÈÍ¤IÉ" "áq]»Ü¸ÙÚInÊ7Ø" "p¸®Þ{ão4zG+Ð" "s#X¢Iª^x" ",g" "r\\Û" "yÈFiÏnD~ßæw"] :ref-cutstack :scalars-return #{20.05078125 118.453125 :input!2 :input!10 [true] :scalar-π #{:refs-remove [] 97094354M :code-cons :booleans-take [356 3582 1655 3985 2768 1922 1230] :strings-flush 471335139} (true (:string-first [false true true false true false false false false] :exec-when [243] :code-wrap) (927680944 :set-subset? :scalars-new 965589770M [0.04296875 0.03125]) :strings-build [true false])} :string-conjchar ["ÀÌeyi#Q·" "Í#]¶k·­µGXÂE¼£" "©²E)°" "ÊR ]ZrÑÍ¬ÝÐ" "otO2lIÉ¢Iáz)" "B!" "S;Á+±" "¤ÜqÊ0'Ü*" "Ø9ÆÏ­9¾AºråÓÛbL¬a"] #{:scalars-butlast #{:set-cutflip 96/101 ["»\"\\¾ãhKm" "ßÀA<×fÞ£q~Ê&Êä+ÃGg2" "µ\\¡«{J¨Ö:Îªk" "¶å]¡Õ`/:K" "t~»Íç\\}X" "½½mÈQ:)Jn"] true :strings-yank [\space \Á \/] #{"ºÖKÆ¸¾}GqR$ÉI" :chars-cutstack "·" 493606988M :scalar-flush :refs-contains? :exec-yankdup 95597047} :input!5} :exec-stackdepth :scalar-arcsine :scalar-shove :strings-emptyitem? :booleans-store ([\j \{ \t] \Y :scalar-some [] :booleans-replacefirst)} [0.046875 0.015625 0.12109375 0.02734375 0.1484375 0.01953125 0.1484375 0.0546875] :vector-do*each :input!4 "mfã¨ÆÆkÑt®" :string-spacey? :exec-print false :boolean-xor :scalars-remove :char-yank :boolean-flipstack 86/175 :input!1 :code-storestack \ :scalar-cutflip (:vector-set :booleans-rest ["â²!^Ï[CØi"] :exec-againlater :strings-generalizeall) :tagspace-cutstack 765572360 :scalar-save "5" :strings-take false (:chars-echoall [0.05859375 0.01953125 0.0859375 0.00390625 0.1484375 0.0546875 0.07421875] [true false true false true true true true false] 657341101M :scalar-swap) :tagspace-swap :exec-sampler 63452125M :scalars-echo (:scalars-equal? :vector-comprehension :tagspace-flipstack :scalars-flipstack ["]¸Ù¿OfÏ«r7;ÓpSÒ" "à!:³«©+j: ª|´´"]) :string-return [\ä \W \g \Ê] :tagspace-echoall 173.72265625 39/16 :refs-cutstack :vector-flush :strings-generalizeall 188.1171875 :char<? :char-print 284.6796875 [true true true true true false true] \ 641120072 :tagspace-sampler :chars-echoall :char-swap :boolean-faircoin 437404658M false (#{"ÍlÔÖæ¸·0 @ª," \© 458979827M :refs-dup :refs-stackdepth false [0.00390625 0.14453125 0.07421875 0.01953125 0.08984375 0.06640625] 31148009M} [0.14453125 0.109375 0.14453125] 773320777 401928451M :tagspace-equal?) :scalars-sampler [true true true true true false false] true [false true false false true true false] :string<? :strings-generalizeall :vector-refilterall [false false true true] "=|Å°àâÏ*:hÜ«Î" :vector-new \Ý [true true true] [true false false] 37/25 :booleans-save [\Ö \µ \Û \ç \§ \¼ \X \© \½] [3849 1725 2951 1603 2177] :boolean-xor :exec-cutstack [true] :boolean-shove "¯ÕßEÄUo$>Gv%" #{:integer-uniform :scalar-cutstack true false [\s \) \J \Ü \Î \¼ \ \] :input!7 [0.140625 0.10546875 0.0703125 0.125] :generator-stackdepth} [\Ð \: \°] :generator-savestack \# (:input!8 117343413M 4/77 21.1328125 :boolean-arity2) 179/134 333.57421875 "<:${j.Í2ÈÂ" (:scalar-rerunall :refs-storestack :booleans-later false :ref-save) [true true false false false true true false false] :ref-print :code-cons 772940803 ["¡Ô¡HÒÃlÆª»N|" "ÎØÍ|°!wÈ1ÁÆ" "" "æ{kÄçA%" "8¸D*E ÀÉ¤" "ÀhMc2á»­©¶q¤#" "a£|(1lÄÑE1+6³°âY9" "VmÖµÂM;ÀfÃ½/Öz]]¤Ø"] \ :tagspace-new [true false true] :chars->code false #{:generator-rerunall :scalars-stackdepth [1929] [4619 2561 4417] :input!1 #{["Ø[Å·À6=EPoÙ­" "6?" "ÀØä" "Ü´8Ñ%h@_|ÝO{%¬£" "h&V¾Ø~Û¿&" "¯-8Ú9ËÛÞ¾ÍÐk"] :string-echoall :generator-rerunall :chars-take :input!10 :vector-take :char-whitespace? (360.703125 :input!2 :booleans-last :refs-nth :generator-flush)} (:string-store :ref-flush #{[4209 1297] #{\ ([\s \S \ \ \´ \] :boolean-faircoin :string-shove :scalar-return-pop :boolean-empty?) :input!1 \u false :scalars-cycler :strings-take :scalar-arctangent} :exec->string :refs-save :char->integer [2958] #{[0.06640625 0.08203125 0.015625] :scalars-rerunall [] :set-shove \2 794908207 :strings-tag :boolean->string} ("¸È¥çE$áÇ V×" [] :exec-s 907405080M [\[ \á \8 \× \C \ç \¢])} [false false false true true false] :input!8) :set-cycler} :environment-stackdepth \Ç "y" ["·¿}\"" "°ÉÆ§ÐÞ7ÙnQÎ)"] 40/137 \ :scalars-first :scalars->tagspace 99/8 116.4296875 53/21 ["<Ç{à®7wXs"] [4394 894 156 4157 1257 1774 2607 1531 377] "KË`nEo®(Ao8)Ø{ß" 231357990M :set->tagspace :char-flush "2Ûjà@," (:scalar-arctangent :ref-cutstack :code-do*range \  :boolean->float) :chars-echoall :scalar-arccosine :char≤? "P7" :scalar-ceiling :scalar-ceiling \* :chars-replacefirst :scalar-dup ["×phçlÅÈ!"] :scalars-cycler :chars-shove :chars-emptyitem? :tagspace-equal? :generator-store :input!8 #{357.8828125 :string-replace 726050045 514207010 false ["¡*ËÞÎf" "Ø{PßÇ" "'Ja?AåÃ" "ÅËXæ³" "×·" "Ø¦VxÊßQ" "6:ÈnÀk»ÜÈ" "h«ÛmRªÒf5Ú[¯"] #{92.89453125 [false true] :chars-occurrencesof :string-equal? :tagspace-pop :booleans-sampler ["L£Ó¨t" "²TÕÌGá°" "vä·åVÁ'gæ¨X´Ë" "¦´¹Þ"] [\­ \space \ß]} :input!6} :tagspace-echoall :scalar-liftstack :scalar-cutflip [true true false true false false false] :print-empty? :input!4 624957180M \p 246.77734375 :char-max "{Õ$Øç¿Í" :generator-liftstack :booleans-shove "_²c" :chars-shove 31.73828125 :error-stackdepth :scalar-abs :set-shove [true true false true true] \ä :strings-take :char-notequal? :exec-dup :refs-rest :tagspace-stackdepth [0.14453125 0.11328125 0.0703125] ["e^0sy¿ãçNÆqg" "à£3;O8È¶ ÍlN" "¼½u&ÏA&r0&u+xZ\\Hs" "àæËä^\"Ö¶Îr" "äe,^*W0" "á¿1Ã" "oÄsnH" "ºÃ!8äw"] :refs-first [170 4026 3876 4548 1591 3153 858] (434218364 ["àF[^XºNoW­\\+U" "ÑSÚ¦Ýãm?CknÜhÇA"] :strings-againlater :chars-flipstack :generator->code) :input!2 [] :booleans-first :chars-yankdup 545255940]



    :bindings 

    '{:input!2 ([false true false true true false false true true]), :input!9 (:boolean-return), :input!3 (#{:generator-return [] :code-notequal? :vector-flush 190/131 186/199 #{76.33203125 [] \, "FÕ" :scalar-inc 929423847M [0.09375 0.0859375 0.08984375 0.12109375 0.0625 0.1171875] :tagspace-return-pop}}), :input!10 (#{:exec-print (:scalars-remove :string-cycler "\"" :strings-reverse) :booleans-return [0.14453125 0.01953125 0.0234375 0.125] 16/63 :chars-reverse ["æv+=L$º°4^oBx4" "Âå"] [924 1384 3779 183 186 4511 3864 4113 3736]}), :input!1 (:vector->code), :input!8 (:booleans-dup), :input!4 (:refs-remove), :input!7 (:generator-yankdup), :input!5 (:scalar-echo), :input!6 (#{[\Ñ \space \@ \ \p \æ \h \À] :booleans-generalizeall #{601297577 :strings-pop :boolean-save :boolean-store :refs-remove false [\b \o \¶ \M \W \ \! \4] [\s \Â \I \° \% \¡]} :strings-occurrencesof false :ref-tag :string-length :generator-cutstack})}

}

  ])


(fact "no exceptions are raised when I run any of these problematic programs"
  :debug :acceptance
  (map check-on-prisoner prisoners) =not=> (throws))

