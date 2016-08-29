(ns droit.task1)

;;defs

(def rule1 [[:a :b] :-> :c])

(def rule2 [[:c :d :e] :-> :f])

(def rule3 [[:k :d :e] :-> :m])



(def ruleset [rule1 rule2 rule3] )





;;task 1

(require '[clojure.set :as set])



(defn set-rule-parser [rule]

  (let [[inputs _-> & outputs] rule]

    [(set inputs)

     (set outputs)]))



(defn reducer [[accumulated-input

                accumulated-output]

               [new-input

                new-output]]

  [(set/union accumulated-input new-input)

   (set/union accumulated-output new-output)])



(defn break-out-rules [ruleset]

  (map set-rule-parser ruleset))



(defn task1 [ruleset]

  (let [into-sets (break-out-rules ruleset)]

    (println "into-sets: " into-sets)

    (reduce reducer into-sets)

    ))

;=> into-sets:  ([#{:b :a} #{:c}] [#{:e :c :d} #{:f}] [#{:e :k :d} #{:m}])

;=> [#{:e :k :c :b :d :a} #{:m :c :f}]
