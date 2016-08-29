(ns task2.core
  (:gen-class)
  (:require [clojure.tools.namespace.dependency :as dep]))

;;defs
(def rule1 [[:a :b] :-> :c])
(def rule2 [[:c :d :e] :-> :f])
(def rule3 [[:k :d :e] :-> :m])
(def rule4 [[:p :t] :-> :k])

(def ruleset [rule1 rule2 rule3 rule4])

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
    (reduce reducer into-sets)))


(task1 ruleset)
;into-sets:  ([#{:b :a} #{:c}] [#{:e :c :d} #{:f}] [#{:e :k :d} #{:m}] [#{:t :p} #{:k}])
;=> [#{:e :k :c :b :d :t :p :a} #{:m :k :c :f}]

;;droit
(def rule1 [[:a :b] :-> :c])
(def rule2 [[:c :d :e] :-> :f])
(def rule3 [[:k :d :e] :-> :m])
(def rule4 [[:p :t] :-> :k])

(def ruleset [rule1 rule2 rule3 rule4])

;(defn build-tree [ruleset]
;  (let [into-sets (map set-rule-parser ruleset)]
;    (println "into-sets: " into-sets)
;    (map #(prn %) into-sets)
;    ))
;
;(build-tree ruleset)
;
;(defn build-rule-tree [rule]
;  (let [[lhs rhs] rule]
;    ;(prn lhs :-> rhs)
;    ))

(require '[com.stuartsierra.dependency :as dep])

;(build-rule-tree [#{:b :a} #{:c}])

;(defn build-tree [ruleset]
;  (let [into-sets (map set-rule-parser ruleset)
;        graph (dep/graph)]
;    (println "into-sets: " into-sets)
;    (println graph)
;    (map #(build-rule-tree %) into-sets)
;    ))

(defn set-rule-parser [rule]
  (let [[inputs _-> & outputs] rule]
    [(set inputs)
     (set outputs)]))

(defn break-out-rules [ruleset]
  (map set-rule-parser ruleset))

(defn graph-building-reducer [graph [lhs rhs]]
  (dep/depend graph rhs lhs))

(defn task2 [ruleset]
  (let [into-sets (break-out-rules ruleset)]
    (println "into-sets: " into-sets)
    (reduce graph-building-reducer (dep/graph) into-sets)))


(task2 ruleset)

;=========================

(def rule1 [[:a :b] :-> :c])
(def rule2 [[:c :d :e] :-> :f])
(def rule3 [[:k :d :e] :-> :m])
(def rule4 [[:p :t] :-> :k])

(def ruleset [rule1 rule2 rule3 rule4])

(require '[com.stuartsierra.dependency :as dep])

(defn apply-rule
  [graph rule]
  (let [[premises _ consequence] rule]
    (reduce #(dep/depend %1 consequence %2) graph premises)))

(defn build-graph[ruleset]
  (reduce apply-rule (dep/graph) ruleset))

;(build-graph ruleset)

(def graph (build-graph ruleset))

(dep/transitive-dependents graph :a)
;=> #{:c :f}
(dep/transitive-dependencies graph :c)
;=> #{:b :a}

;===================
(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)])
  graph)

(task2 ruleset [])
;====================

(defn dependents [node]
  (dep/transitive-dependents graph node))

;==========================
(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (map #(dep/transitive-dependents graph %1) facts)]
    (first dependents)))

(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (map #(dep/transitive-dependents graph %1) facts)]
    dependents))

(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (map (partial dep/transitive-dependents graph) facts)]
    dependents))

;=> #'droit.core/droit
(task2 ruleset [:a :b])

;===================
(defn dpdnts-fn [dpdnts fact]
  (assoc dpdnts fact (dep/transitive-dependents graph fact)))

(defn build-dependents[graph facts]
  (reduce dpdnts-fn {} facts))

(build-dependents graph [:a :b])

(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)]
    dependents))

(task2 ruleset [:a :b])
;=> {:a #{:c :f}, :b #{:c :f}}

(def facts-a-b [:a :b])

;walk over dependents of facts
(map #(% (task2 ruleset facts-a-b))facts-a-b)
(map #(% (build-dependents graph [:a])) [:a])

(require '[clojure.set :as set])
;=> nil
(set/subset? #{:a :b} #{:a :b :c})
;=> true

;for-each(fact : facts) {

;  for-each(dependent : dependents)

;    transitive-dependencies = dependent.transitive-dependencies

;       if(= transitive-dependencies facts)

(defn dpdcs-fn [dpdcs fact]
  (assoc dpdcs fact (dep/immediate-dependencies graph fact)))

(defn build-dependencies[graph facts]
  (reduce dpdcs-fn {} facts))

(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        dependents-per-fact (map #(%1 dependents) facts)
        dependencies (map #(build-dependencies graph %1) dependents-per-fact)
        outcomes #{}]
    (map (fn[dependency] (seq dependency)) dependencies)))


(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        dependents-per-fact (map #(%1 dependents) facts)
        dependencies (map #(build-dependencies graph %1) dependents-per-fact)
        outcomes #{}]
    (reduce fltr #{} dependencies)))

(defn fltr[accum dpdc]
  (let [key (key (clojure.lang.MapEntry. dpdc))
        value (val (clojure.lang.MapEntry. dpdc))] println ("key: " key "value: " value)))


(defn fltr [accum dpdcs-map facts]
  (for [[k v] {:c #{:b :a}, :f #{:e :c :b :d :a}}]
    ;(println "key: " k "value: " v)
    (if (= facts v) (conj accum k) nil)))



(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        dependents-per-fact (into [] (into #{}(map #(%1 dependents) facts)))
        dependencies (map #(build-dependencies graph %1) dependents-per-fact)
        outcomes #{}]
    ;(for[[k v ] dependencies]
    ; (if(= facts v)(conj outcomes k) nil))
    dependencies))

(task2 ruleset #{:a :b})
;=> ({:c #{:b :a}, :f #{:e :c :b :d :a}})

(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        dependents-per-fact (into [] (into #{}(map #(%1 dependents) facts)))
        dependencies (map #(build-dependencies graph %1) dependents-per-fact)
        outcomes #{}]

    (for[[k v ] (into {} dependencies)]
      (println "key: " k "value: " v))))
      ;(if(= facts v)(conj outcomes k) nil))



(task2 ruleset #{:a :b})
key:  :c value:  #{:b :a}
key:  :f value:  #{:e :c :b :d :a}
;=> (nil nil)

(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        dependents-per-fact (into [] (into #{}(map #(%1 dependents) facts)))
        dependencies (map #(build-dependencies graph %1) dependents-per-fact)
        outcomes #{}]

    (for[[node deps ] (into {} dependencies)]
      ;(println "key: " node "value: " deps)
      (if(= facts deps)(conj outcomes node) nil))))


;=> #'droit.core/droit
(task2 ruleset #{:a :b})
;=> (#{:c} nil)

(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        dependents-per-fact (into [] (into #{}(map #(%1 dependents) facts)))
        dependencies (map #(build-dependencies graph %1) dependents-per-fact)
        outcomes #{}]

    (for[[k v ] (into {} dependencies)]
      ;(println "key: " k "value: " v)
      (if(= facts v)(conj outcomes k) outcomes))))


;=> #'droit.core/droit
(task2 ruleset #{:a :b})
;=> (#{:c} #{})

(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        ;dependents-per-fact (into [] (into #{}(map #(%1 dependents) facts)))
        dependencies (map #(build-dependencies graph %1) (into [] (into #{}(map #(%1 dependents) facts))))
        outcomes #{}]

    (for[[k v ] (into {} dependencies)]
      ;(println "key: " k "value: " v)
      (if(= facts v)(conj outcomes k) outcomes))))


;=> #'droit.core/droit
(task2 ruleset #{:a :b})
;=> (#{:c} #{})
(task2 ruleset #{:a :b :c :d :e})
;=> (#{} #{:f} #{})


(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        dependents-per-fact (into [] (into #{}(map #(%1 dependents) facts)))
        dependencies (map #(build-dependencies graph %1) dependents-per-fact)
        ;dependencies (map #(build-dependencies graph %1) (into [] (into #{}(map #(%1 dependents) facts))))
        outcomes #{}]

    (println "dependents: " dependents)
    (println "dependents-per-fact: " dependents-per-fact)
    (println "dependencies: " dependencies)

    (for[[k v ] (into {} dependencies)]
      (println "key: " k "value: " v))))
      ;(if(= facts v)(conj outcomes k) outcomes)


;=> #'droit.core/droit
(task2 ruleset #{:a :b :c :d :e})
dependents:  {:e #{:m :f}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}}
dependents-per-fact:  [#{:m :f} #{:c :f} #{:f}]
dependencies:  ({:m #{:e :k :d :t :p}, :f #{:e :c :b :d :a}} {:c #{:b :a}, :f #{:e :c :b :d :a}} {:f #{:e :c :b :d :a}})
key:  :m value:  #{:e :k :d :t :p}
key:  :f value:  #{:e :c :b :d :a}
key:  :c value:  #{:b :a}
;=> (nil nil nil)

;============= 4 different ways to flatten a map ============
(defn apply-flatten [flattened dpdnts]

  (let [[key val] dpdnts]
    (reduce conj flattened val)))

(defn flatten-dpdnts [dpdts-map]
  (reduce apply-flatten #{} dpdts-map))

(flatten-dpdnts {:e #{:m :f}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}})
;=> #{:m :c :f}
;;============================================================

(defn flatten-dpdnts [dpdnts-map]
  (apply set/union (vals dpdnts-map)))

(flatten-dpdnts {:e #{:m :f}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}})
;=> #{:m :c :f}

;;=============================================================
(reduce (fn[ flattened [key val]]
          (clojure.set/union flattened val))
        #{}
        {:e #{:m :f}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}})

;=> #{:m :c :f}

;;=============================================================
(defn apply-flatten [flattened dpdnts]

  (let [[key val] dpdnts]
    (into flattened val)))

(defn flatten-dpdnts [dpdts-map]
  (reduce apply-flatten #{} dpdts-map))

(flatten-dpdnts {:e #{:m :f}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}})
;;=> #{:m :c :f}

;;======================================

(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        ;dependents-per-fact (into [] (into #{}(map #(%1 dependents) facts)))
        flattened (apply set/union (vals dependents))
        dependencies (build-dependencies graph flattened)
        ;dependencies (map #(build-dependencies graph %1) (into [] (into #{}(map #(%1 dependents) facts))))
        outcomes #{}]

    (println "dependents: " dependents)
    (println "flattened: " flattened)
    (println "dependencies: " dependencies)

    (for[[k v ] (into {} dependencies)]
      (println "key: " k "value: " v))))
      ;(if(= facts v)(conj outcomes k) outcomes)



(task2 ruleset #{:a :b :c :d :e})
;;dependents:  {:e #{:m :f}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}}
;;flattened:  #{:m :c :f}
;;dependencies:  {:m #{:e :k :d :t :p}, :c #{:b :a}, :f #{:e :c :b :d :a}}
;;key:  :m value:  #{:e :k :d :t :p}
;;key:  :c value:  #{:b :a}
;;key:  :f value:  #{:e :c :b :d :a}
;;=> (nil nil nil)

(filter (comp #(= #{:a :b :c :d :e} %) last) {:m #{:e :k :d :t :p}, :c #{:b :a}, :f #{:e :c :b :d :a}})
;;=> ([:f #{:e :c :b :d :a}])
(map first (filter (comp #(= #{:a :b :c :d :e} %) last) {:m #{:e :k :d :t :p}, :c #{:b :a}, :f #{:e :c :b :d :a}}))
;;=> (:f)

;;==========================================

(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        flattened (apply set/union (vals dependents))
        dependencies (build-dependencies graph flattened)]


    (println "dependents: " dependents)
    (println "flattened: " flattened)
    (println "dependencies: " dependencies)

    (map first (filter (comp #(set/subset? % facts) last) dependencies))))



(task2 ruleset #{:a :b :c :d :e :t :p})
;;dependents:  {:e #{:m :f}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :t #{:m :k}, :p #{:m :k}, :a #{:c :f}}
;;flattened:  #{:m :k :c :f}
;;dependencies:  {:m #{:e :k :d :t :p}, :k #{:t :p}, :c #{:b :a}, :f #{:e :c :b :d :a}}
;;=> (:k :c :f)
(task2 ruleset #{:a :b :c :d :e})
;;dependents:  {:e #{:m :f}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}}
;;flattened:  #{:m :c :f}
;;dependencies:  {:m #{:e :k :d :t :p}, :c #{:b :a}, :f #{:e :c :b :d :a}}
;;=> (:c :f)
(task2 ruleset #{:a :b})
;;dependents:  {:b #{:c :f}, :a #{:c :f}}
;;flattened:  #{:c :f}
;;dependencies:  {:c #{:b :a}, :f #{:e :c :b :d :a}}
;;=> (:c)
(task2 ruleset #{:a :b :c :d :e :k}) ;bad, should be (:c :f :m)
;;dependents:  {:e #{:m :f}, :k #{:m}, :c #{:f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}}
;;flattened:  #{:m :c :f}
;;dependencies:  {:m #{:e :k :d :t :p}, :c #{:b :a}, :f #{:e :c :b :d :a}}
;;=> (:c :f)
(task2 ruleset #{:a :b :d :e}) ;bad, should be (:c :f)
;;dependents:  {:e #{:m :f}, :b #{:c :f}, :d #{:m :f}, :a #{:c :f}}
;;flattened:  #{:m :c :f}
;;dependencies:  {:m #{:e :k :d :t :p}, :c #{:b :a}, :f #{:e :c :b :d :a}}
;;=> (:c)

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

;;=================== full task 2 ======================

(def rule1 [[:a :b] :-> :c])
(def rule2 [[:c :d :e] :-> :f])
(def rule3 [[:k :d :e] :-> :m])
(def rule4 [[:p :t] :-> :k])
(def rule5 [[:a :b] :-> :x])
(def rule6 [[:k :t] :-> :x])

(def ruleset [rule1 rule2 rule3 rule4])

(require '[clojure.set :as set])
(require '[com.stuartsierra.dependency :as dep])



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

(defn task2 [ruleset facts]
  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        flattened (apply set/union (vals dependents))
        dependencies (build-dependencies graph flattened)]


    (println "dependents: " dependents)
    (println "flattened: " flattened)
    (println "dependencies: " dependencies)

    (map first (filter (comp #(set/subset? % facts) last) dependencies))))





(defn task2 [ruleset facts]

  (let [graph (reduce apply-rule (dep/graph) ruleset)
        dependents (build-dependents graph facts)
        flattened (apply set/union (vals dependents))
        dependencies (into (sorted-map)(build-dependencies graph flattened))]


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

    (match facts dependencies)))



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
(task2 ruleset #{:a :b})
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
;;======================================================
(task2 ruleset #{:a :b :k :t})