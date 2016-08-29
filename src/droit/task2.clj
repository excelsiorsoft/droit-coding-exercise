(ns droit.task2
  (:require [clojure.tools.namespace.dependency :as dep]))

(def rule1 [[:a :b] :-> :c])

(def rule2 [[:c :d :e] :-> :f])

(def rule3 [[:k :d :e] :-> :m])

(def rule4 [[:p :t] :-> :k])

(def ruleset [rule1 rule2 rule3 rule4] )

(require '[clojure.set :as set])

;(require '[com.stuartsierra.dependency :as dep])

(defn apply-rule

  [graph rule]

  (let [[premises _ consequence] rule]

    (reduce #(dep/depend %1 consequence %2) graph premises)))

(defn build-graph[ruleset]

  (reduce apply-rule (dep/graph) ruleset))

(def graph (build-graph ruleset))



(defn dpdnts-fn [dpdnts fact]

  (assoc dpdnts fact (dep/transitive-dependents graph fact)))

(defn build-dependents[graph facts]

  (reduce dpdnts-fn {} facts))

(defn build-dependents[graph facts]

  (reduce dpdnts-fn {} facts))

(defn dpdcs-fn [dpdcs fact]

  (assoc dpdcs fact (dep/immediate-dependencies graph fact)))

(defn build-dependencies[graph facts]

  (reduce dpdcs-fn {} facts))

;(defn task2 [ruleset facts]
;
;  (let [graph (reduce apply-rule (dep/graph) ruleset)
;
;        dependents (build-dependents graph facts)
;
;        flattened (apply set/union (vals dependents))
;
;        dependencies (build-dependencies graph flattened)
;
;        ]
;
;    (println "dependents: " dependents)
;
;    (println "flattened: " flattened)
;
;    (println "dependencies: " dependencies)
;
;    (map first (filter (comp #(set/subset? % facts) last) dependencies))
;
;    ))

(defn match [facts dependencies]

  (loop [acc #{}

         facts facts]

    (let [[acc' facts'] (reduce (fn [[acc' facts'] [k v]]

                                  (if (set/subset? v facts)

                                    [(conj acc' k) (conj facts' k)]

                                    [acc' facts']))

                                [acc facts]

                                dependencies)]

      (if (= facts facts')

        acc'

        (recur acc' facts')))))

(defn task2 [ruleset facts]

  (let [graph (reduce apply-rule (dep/graph) ruleset)

        dependents (build-dependents graph facts)

        flattened (apply set/union (vals dependents))

        dependencies (into (sorted-map)(build-dependencies graph flattened))

        ]

    (println "dependents: " dependents)

    (println "flattened: " flattened)

    (println "dependencies: " dependencies)

    ;(map first (filter (comp #(set/subset? % facts) last) dependencies))

    ;(first (reduce (fn [[acc facts] [k v]]

    ;

    ;                 (if (set/subset? v facts)

    ;                   [(conj acc k) (conj facts k)]

    ;                   [acc facts]))

    ;               [#{} facts]

    ;               dependencies))



    (match facts dependencies)

    ))



;;=============================

(task2 ruleset #{:a :b :c :d :e :t :p})

;;dependents:  {:e #{:m :f}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :t #{:m :k}, :p #{:m :k}, :a #{:c :f}}

;;flattened:  #{:m :k :c :f}

;;dependencies:  {:c #{:b :a}, :f #{:e :c :d}, :k #{:t :p}, :m #{:e :k :d}}

;;=> #{:m :k :c :f}

(task2 ruleset #{:a :b :c :d :e})

;;dependents:  {:e #{:m :f}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}}

;;flattened:  #{:m :c :f}

;;dependencies:  {:c #{:b :a}, :f #{:e :c :d}, :m #{:e :k :d}}

;;=> #{:c :f}

(task2 ruleset #{:a :b })

;;dependents:  {:b #{:c :f}, :a #{:c :f}}

;;flattened:  #{:c :f}

;;dependencies:  {:c #{:b :a}, :f #{:e :c :d}}

;;=> #{:c}

(task2 ruleset #{:a :b :c :d :e :k})

;;dependents:  {:e #{:m :f}, :k #{:m}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}}

;;flattened:  #{:m :c :f}

;;dependencies:  {:c #{:b :a}, :f #{:e :c :d}, :m #{:e :k :d}}

;;=> #{:m :c :f}

(task2 ruleset #{:a :b :d :e})

;;dependents:  {:e #{:m :f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}}

;;flattened:  #{:m :c :f}

;;dependencies:  {:c #{:b :a}, :f #{:e :c :d}, :m #{:e :k :d}}

;;=> #{:c :f}
