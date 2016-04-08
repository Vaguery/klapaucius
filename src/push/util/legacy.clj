(ns push.util.legacy
  (:use midje.sweet)
  (:require [clojure.zip :as zip]))


(def clojush-boolean
  { 'boolean_and                       :boolean-and
    'boolean_or                        :boolean-or
    'boolean_not                       :boolean-not
    'boolean_xor                       :boolean-xor
    'boolean_invert_first_then_and     :not-supported-boolean_invert_first_then_and
    'boolean_invert_second_then_and    :not-supported-boolean_invert_second_then_and
    'boolean_frominteger               :integer->boolean
    'boolean_fromfloat                 :float->boolean

    'boolean_dup                       :boolean-dup
    'boolean_empty                     :boolean-empty?
    'boolean_eq                        :boolean-equal?
    'boolean_flush                     :boolean-flush
    'boolean_pop                       :boolean-pop
    'boolean_rot                       :boolean-rotate
    'boolean_shove                     :boolean-shove
    'boolean_stackdepth                :boolean-stackdepth
    'boolean_swap                      :boolean-swap
    'boolean_yank                      :boolean-yank
    'boolean_yankdup                   :boolean-yankdup
  })


(def clojush-char
  { 'char_allfromstring     :string->chars
    'char_frominteger       :integer->char
    'char_fromfloat         :float->char
    'char_isletter          :char-letter?
    'char_isdigit           :char-digit?
    'char_iswhitespace      :char-whitespace?

    'char_dup               :char-dup
    'char_empty             :char-empty?
    'char_eq                :char-equal?
    'char_flush             :char-flush
    'char_pop               :char-pop
    'char_rot               :char-rotate
    'char_shove             :char-shove
    'char_stackdepth        :char-stackdepth
    'char_swap              :char-swap
    'char_yank              :char-yank
    'char_yankdup           :char-yankdup
  })


(def clojush-code
  { 'code_append            :code-append
    'code_atom              :code-atom?
    'code_car               :code-first
    'code_cdr               :code-rest
    'code_cons              :code-cons
    'code_container         :code-container
    'code_contains          :code-contains?
    'code_do                :code-do
    'code_do*               :code-do*
    'code_do*count          :code-do*count
    'code_do*range          :code-do*range
    'code_do*times          :code-do*times
    'code_extract           :code-extract
    'code_fromboolean       :boolean->code
    'code_fromfloat         :float->code
    'code_frominteger       :integer->code
    'code_if                :code-if
    'code_insert            :code-insert
    'code_length            :code-length
    'code_list              :code-list
    'code_map               :code-map
    'code_member            :code-member?
    'code_noop              :code-noop
    'code_nth               :code-nth
    'code_nthcdr            :code-drop
    'code_null              :code-null?
    'code_position          :code-position
    'code_quote             :exec->code
    'code_size              :code-points
    'code_subst             :code-subst
    'code_wrap              :code-wrap

    'code_dup               :code-dup
    'code_empty             :code-empty?
    'code_eq                :code-equal?
    'code_flush             :code-flush
    'code_pop               :code-flush
    'code_rot               :code-rotate
    'code_shove             :code-shove
    'code_stackdepth        :code-stackdepth
    'code_swap              :code-swap
    'code_yank              :code-yank
    'code_yankdup           :code-yankdup
  })



(def clojush-exec
  { 'exec_do*count    :exec-do*count
    'exec_do*range    :exec-do*range
    'exec_do*times    :exec-do*times
    'exec_do*while    :exec-do*while
    'exec_if          :exec-if
    'exec_k           :exec-k
    'exec_noop        :exec-noop
    'exec_s           :exec-s
    'exec_when        :exec-when
    'exec_while       :exec-while
    'exec_y           :exec-y

    'exec_dup         :exec-dup
    'exec_empty       :exec-empty?
    'exec_eq          :exec-equal?
    'exec_flush       :exec-flush
    'exec_pop         :exec-pop
    'exec_rot         :exec-rotate
    'exec_shove       :exec-shove
    'exec_stackdepth  :exec-stackdepth
    'exec_swap        :exec-swap
    'exec_yank        :exec-yank
    'exec_yankdup     :exec-yankdup
  })



(def clojush-environment
  { 'environment_begin   :environment-begin
    'environment_end     :environment-end
    'environment_new     :environment-new
  })


(def clojush-plush
  { 'noop_delete_prev_paren_pair   :not-supported-noop_delete_prev_paren_pair
    'noop_open_paren               :not-supported-noop_open_paren
  })


(def clojush-print
  { 'print_boolean         :boolean-print
    'print_char            :char-print
    'print_code            :code-print
    'print_exec            :exec-print
    'print_float           :float-print
    'print_integer         :integer-print
    'print_newline         :print-newline
    'print_string          :string-print
    'print_vector_boolean  :booleans-print
    'print_vector_float    :floats-print
    'print_vector_integer  :integers-print
    'print_vector_string   :strings-print
  })



(def clojush-float
  { 'float_add          :float-add
    'float_cos          :float-cosine
    'float_dec          :float-dec
    'float_div          :float-divide
    'float_fromboolean  :boolean->float
    'float_fromchar     :char->float
    'float_frominteger  :integer->float
    'float_fromstring   :string->float
    'float_gt           :float>?
    'float_gte          :float≥?
    'float_inc          :float-inc
    'float_lt           :float<?
    'float_lte          :float≤?
    'float_max          :float-max
    'float_min          :float-min
    'float_mod          :float-mod
    'float_mult         :float-multiply
    'float_sin          :float-sine
    'float_sub          :float-subtract
    'float_tan          :float-tangent

    'float_dup          :float-dup
    'float_empty        :float-empty?
    'float_eq           :float-equal?
    'float_flush        :float-flush
    'float_pop          :float-pop
    'float_rot          :float-rotate
    'float_shove        :float-shove
    'float_stackdepth   :float-stackdepth
    'float_swap         :float-swap
    'float_yank         :float-yank
    'float_yankdup      :float-yankdup
  })



(def clojush-integer
  { 'integer_add           :integer-add
    'integer_dec           :integer-dec
    'integer_div           :integer-divide
    'integer_fromboolean   :boolean->integer
    'integer_fromchar      :char->integer
    'integer_fromfloat     :float->integer
    'integer_fromstring    :string->integer
    'integer_gt            :integer>?
    'integer_gte           :integer≥?
    'integer_inc           :integer-inc
    'integer_lt            :integer<?
    'integer_lte           :integer≤?
    'integer_max           :integer-max
    'integer_min           :integer-min
    'integer_mod           :integer-mod
    'integer_mult          :integer-multiply
    'integer_sub           :integer-subtract

    'integer_dup           :integer-dup
    'integer_empty         :integer-empty?
    'integer_eq            :integer-equal?
    'integer_flush         :integer-flush
    'integer_pop           :integer-pop
    'integer_rot           :integer-rotate
    'integer_shove         :integer-shove
    'integer_stackdepth    :integer-stackdepth
    'integer_swap          :integer-swap
    'integer_yank          :integer-yank
    'integer_yankdup       :integer-yankdup
  })



;; the push-in-clojure random functions are designed VERY differently from Clojush's

(def clojush-rand
  { 'boolean_rand       :not-supported-boolean_rand
    'integer_rand       :not-supported-integer_rand        
    'float_rand         :not-supported-float_rand      
    'code_rand          :not-supported-code_rand     
    'string_rand        :not-supported-string_rand       
    'char_rand          :not-supported-char_rand     
  })


(def clojush-return
  { 'return_fromexec      :exec-return
    'return_frominteger   :integer-return
    'return_fromfloat     :float-return
    'return_fromboolean   :boolean-return
    'return_fromstring    :string-return
    'return_fromchar      :char-return
    'return_fromcode      :code-return
    'return_exec_pop      :exec-return-pop
    'return_code_pop      :code-return-pop
    'return_integer_pop   :integer-return-pop
    'return_float_pop     :float-return-pop
    'return_boolean_pop   :boolean-return-pop
    'return_zip_pop       :zip-return-pop
    'return_string_pop    :string-return-pop
    'return_char_pop      :char-return-pop
    'return_tagspace      :not-supported-return_tagspace
  })



(def clojush-string
  { 'exec_string_iterate        :exec-string-iterate
    'string_butlast             :string-butlast
    'string_concat              :string-concat
    'string_conjchar            :string-conjchar
    'string_contains            :string-contains?
    'string_containschar        :string-containschar?
    'string_emptystring         :string-emptystring?
    'string_first               :string-first
    'string_fromboolean         :boolean->string
    'string_fromchar            :char->string
    'string_fromfloat           :float->string
    'string_frominteger         :integer->string
    'string_indexofchar         :string-indexofchar
    'string_last                :string-last
    'string_length              :string-length
    'string_nth                 :string-nth
    'string_occurrencesofchar   :string-occurrencesofchar
    'string_parse_to_chars      :string-shatter
    'string_removechar          :string-removechar
    'string_replace             :string-replace
    'string_replacechar         :string-replacechar
    'string_replacefirst        :string-replacefirst
    'string_replacefirstchar    :string-replacefirstchar
    'string_rest                :string-rest
    'string_reverse             :string-reverse
    'string_setchar             :string-setchar
    'string_split               :string-splitonspaces
    'string_substring           :string-substring
    'string_take                :strig-take

    'string_dup                 :string-dup
    'string_empty               :string-empty?
    'string_eq                  :string-equal?
    'string_flush               :string-flush
    'string_pop                 :string-pop
    'string_rot                 :string-rot
    'string_shove               :string-shove
    'string_stackdepth          :string-stackdepth
    'string_swap                :string-swap
    'string_yank                :string-yank
    'string_yankdup             :string-yankdup
  })


(def clojush-vector-boolean
  { 'exec_do*vector_boolean        :booleans-do*each
    'vector_boolean_butlast        :booleans-butlast
    'vector_boolean_concat         :booleans-concat
    'vector_boolean_conj           :booleans-conj
    'vector_boolean_contains       :booleans-contains?
    'vector_boolean_dup            :booleans-dup
    'vector_boolean_empty          :booleans-empty?
    'vector_boolean_emptyvector    :booleans-emptyitem?
    'vector_boolean_eq             :booleans-equal?
    'vector_boolean_first          :booleans-first
    'vector_boolean_flush          :booleans-flush
    'vector_boolean_indexof        :booleans-indexof
    'vector_boolean_last           :booleans-last
    'vector_boolean_length         :booleans-length
    'vector_boolean_nth            :booleans-nth
    'vector_boolean_occurrencesof  :booleans-occurrencesof
    'vector_boolean_pop            :booleans-pop
    'vector_boolean_pushall        :booleans-shatter
    'vector_boolean_remove         :booleans-remove
    'vector_boolean_replace        :booleans-replace
    'vector_boolean_replacefirst   :booleans-replacefirst
    'vector_boolean_rest           :booleans-rest
    'vector_boolean_reverse        :booleans-reverse
    'vector_boolean_rot            :booleans-rotate
    'vector_boolean_set            :booleans-set
    'vector_boolean_shove          :booleans-shove
    'vector_boolean_stackdepth     :booleans-stackdepth
    'vector_boolean_subvec         :booleans-portion
    'vector_boolean_swap           :booleans-swap
    'vector_boolean_take           :booleans-take
    'vector_boolean_yank           :booleans-yank
    'vector_boolean_yankdup        :booleans-yankdup
  })


(def clojush-vector-integer
  { 'exec_do*vector_integer        :integers-do*each
    'vector_integer_butlast        :integers-butlast
    'vector_integer_concat         :integers-concat
    'vector_integer_conj           :integers-conj
    'vector_integer_contains       :integers-contains?
    'vector_integer_dup            :integers-dup
    'vector_integer_empty          :integers-empty?
    'vector_integer_emptyvector    :integers-emptyitem?
    'vector_integer_eq             :integers-equal?
    'vector_integer_first          :integers-first
    'vector_integer_flush          :integers-flush
    'vector_integer_indexof        :integers-indexof
    'vector_integer_last           :integers-last
    'vector_integer_length         :integers-length
    'vector_integer_nth            :integers-nth
    'vector_integer_occurrencesof  :integers-occurrencesof
    'vector_integer_pop            :integers-pop
    'vector_integer_pushall        :integers-shatter
    'vector_integer_remove         :integers-remove
    'vector_integer_replace        :integers-replace
    'vector_integer_replacefirst   :integers-replacefirst
    'vector_integer_rest           :integers-rest
    'vector_integer_reverse        :integers-reverse
    'vector_integer_rot            :integers-rotate
    'vector_integer_set            :integers-set
    'vector_integer_shove          :integers-shove
    'vector_integer_stackdepth     :integers-stackdepth
    'vector_integer_subvec         :integers-portion
    'vector_integer_swap           :integers-swap
    'vector_integer_take           :integers-take
    'vector_integer_yank           :integers-yank
    'vector_integer_yankdup        :integers-yankdup
  })


(def clojush-vector-float
  { 'exec_do*vector_float        :floats-do*each
    'vector_float_butlast        :floats-butlast
    'vector_float_concat         :floats-concat
    'vector_float_conj           :floats-conj
    'vector_float_contains       :floats-contains?
    'vector_float_dup            :floats-dup
    'vector_float_empty          :floats-empty?
    'vector_float_emptyvector    :floats-emptyitem?
    'vector_float_eq             :floats-equal?
    'vector_float_first          :floats-first
    'vector_float_flush          :floats-flush
    'vector_float_indexof        :floats-indexof
    'vector_float_last           :floats-last
    'vector_float_length         :floats-length
    'vector_float_nth            :floats-nth
    'vector_float_occurrencesof  :floats-occurrencesof
    'vector_float_pop            :floats-pop
    'vector_float_pushall        :floats-shatter
    'vector_float_remove         :floats-remove
    'vector_float_replace        :floats-replace
    'vector_float_replacefirst   :floats-replacefirst
    'vector_float_rest           :floats-rest
    'vector_float_reverse        :floats-reverse
    'vector_float_rot            :floats-rotate
    'vector_float_set            :floats-set
    'vector_float_shove          :floats-shove
    'vector_float_stackdepth     :floats-stackdepth
    'vector_float_subvec         :floats-portion
    'vector_float_swap           :floats-swap
    'vector_float_take           :floats-take
    'vector_float_yank           :floats-yank
    'vector_float_yankdup        :floats-yankdup
  })


(def clojush-vector-string
  { 'exec_do*vector_string        :strings-do*each
    'vector_string_butlast        :strings-butlast
    'vector_string_concat         :strings-concat
    'vector_string_conj           :strings-conj
    'vector_string_contains       :strings-contains?
    'vector_string_dup            :strings-dup
    'vector_string_empty          :strings-empty?
    'vector_string_emptyvector    :strings-emptyitem?
    'vector_string_eq             :strings-equal?
    'vector_string_first          :strings-first
    'vector_string_flush          :strings-flush
    'vector_string_indexof        :strings-indexof
    'vector_string_last           :strings-last
    'vector_string_length         :strings-length
    'vector_string_nth            :strings-nth
    'vector_string_occurrencesof  :strings-occurrencesof
    'vector_string_pop            :strings-pop
    'vector_string_pushall        :strings-shatter
    'vector_string_remove         :strings-remove
    'vector_string_replace        :strings-replace
    'vector_string_replacefirst   :strings-replacefirst
    'vector_string_rest           :strings-rest
    'vector_string_reverse        :strings-reverse
    'vector_string_rot            :strings-rotate
    'vector_string_set            :strings-set
    'vector_string_shove          :strings-shove
    'vector_string_stackdepth     :strings-stackdepth
    'vector_string_subvec         :strings-portion
    'vector_string_swap           :strings-swap
    'vector_string_take           :strings-take
    'vector_string_yank           :strings-yank
    'vector_string_yankdup        :strings-yankdup
  })


(def clojush-zip
  { 'code_fromzipchildren        :not-supported-code_fromzipchildren
    'code_fromziplefts           :not-supported-code_fromziplefts                     
    'code_fromzipnode            :not-supported-code_fromzipnode                    
    'code_fromziprights          :not-supported-code_fromziprights                      
    'code_fromziproot            :not-supported-code_fromziproot                    
    'exec_fromzipchildren        :not-supported-exec_fromzipchildren                        
    'exec_fromziplefts           :not-supported-exec_fromziplefts                     
    'exec_fromzipnode            :not-supported-exec_fromzipnode                    
    'exec_fromziprights          :not-supported-exec_fromziprights                      
    'exec_fromziproot            :not-supported-exec_fromziproot                    
    'zip_append_child_fromcode   :not-supported-zip_append_child_fromcode
    'zip_append_child_fromexec   :not-supported-zip_append_child_fromexec
    'zip_branch?                 :not-supported-zip_branch?               
    'zip_down                    :not-supported-zip_down            
    'zip_dup                     :not-supported-zip_dup           
    'zip_empty                   :not-supported-zip_empty             
    'zip_end?                    :not-supported-zip_end?            
    'zip_eq                      :not-supported-zip_eq          
    'zip_flush                   :not-supported-zip_flush             
    'zip_fromcode                :not-supported-zip_fromcode                
    'zip_fromexec                :not-supported-zip_fromexec                
    'zip_insert_child_fromcode   :not-supported-zip_insert_child_fromcode
    'zip_insert_child_fromexec   :not-supported-zip_insert_child_fromexec
    'zip_insert_left_fromcode    :not-supported-zip_insert_left_fromcode                            
    'zip_insert_left_fromexec    :not-supported-zip_insert_left_fromexec                            
    'zip_insert_right_fromcode   :not-supported-zip_insert_right_fromcode
    'zip_insert_right_fromexec   :not-supported-zip_insert_right_fromexec
    'zip_left                    :not-supported-zip_left            
    'zip_leftmost                :not-supported-zip_leftmost                
    'zip_next                    :not-supported-zip_next            
    'zip_pop                     :not-supported-zip_pop           
    'zip_prev                    :not-supported-zip_prev            
    'zip_remove                  :not-supported-zip_remove              
    'zip_replace_fromcode        :not-supported-zip_replace_fromcode                        
    'zip_replace_fromexec        :not-supported-zip_replace_fromexec                        
    'zip_right                   :not-supported-zip_right             
    'zip_rightmost               :not-supported-zip_rightmost                 
    'zip_rot                     :not-supported-zip_rot           
    'zip_shove                   :not-supported-zip_shove             
    'zip_stackdepth              :not-supported-zip_stackdepth                  
    'zip_swap                    :not-supported-zip_swap            
    'zip_up                      :not-supported-zip_up          
    'zip_yank                    :not-supported-zip_yank            
    'zip_yankdup                 :not-supported-zip_yankdup               
  })


(def merged-dictionary
  (merge
    clojush-boolean
    clojush-char
    clojush-code
    clojush-environment
    clojush-exec
    clojush-float
    clojush-integer
    clojush-plush
    clojush-print
    clojush-rand
    clojush-return
    clojush-string
    clojush-vector-boolean
    clojush-vector-float
    clojush-vector-integer
    clojush-vector-string
    clojush-zip))


(defn translate-input
  "takes a symbol (which it assumes in an input!) and makes it a keyword; does no validation"
  [item]
  (keyword item))


(defn translate-instruction
  [item]
  (if (symbol? item)
    (get merged-dictionary item (keyword (str "unrecognized-" item)))
    (throw (Exception. "cannot translate items that aren't symbols"))))


(defn translate-item
  [item]
  (cond 
    (and (symbol? item) (re-seq #"^in\d+" (str item)))
      (translate-input item)
    (symbol? item)
      (translate-instruction item)
    (list? item)
      (map translate-item item)
    :else item
    ))


(defn clojush->klapaucius
  [program]
  (into [] (translate-item program)))

