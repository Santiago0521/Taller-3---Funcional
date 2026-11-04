# Informe de corrección

## 1. Introducción

En este documento se argumenta la corrección de las funciones implementadas para consultar y resumir un registro sísmico.

El registro se representa mediante una lista de elementos de tipo `Sismo`:

```scala
type Registro = List[Sismo]
```

Cada sismo contiene una fecha, una región, una magnitud y una profundidad. Las funciones deben producir consultas, agrupamientos y resúmenes que respeten las condiciones establecidas en el enunciado.

Para demostrar su corrección se utilizan principalmente dos herramientas:

- **Inducción estructural sobre listas:** permite demostrar que las funciones que recorren el registro producen el resultado esperado.
- **Propiedades de las operaciones sobre colecciones:** permiten justificar los agrupamientos, las consultas seguras y los resultados obtenidos mediante mapas.

Para las secuencias perezosas se utiliza un argumento por inducción sobre la cantidad de elementos solicitados.

## 2. Punto 1: consultas con expresiones `for`

### 2.1. Función `fuertes`

La función `fuertes(r, umbral)` devuelve las regiones de los sismos cuya magnitud supera el umbral indicado. Debe conservar el orden original de aparición y no eliminar regiones repetidas.

Sea \(F(r,u)\) el resultado de aplicar la función a un registro \(r\) y un umbral \(u\).

Su especificación es:

$$
F(r,u) =
[\ s.region \mid s \in r \land s.magnitud > u\ ]
$$

La notación indica que se conservan, en el orden original, las regiones de todos los sismos que cumplen la condición.

**Demostración por inducción estructural sobre el registro:**

**Caso base:** registro vacío.

Si \(r = Nil\), no existen sismos que puedan superar el umbral. Por tanto:

$$
F(Nil,u) = Nil
$$

El resultado es correcto porque no hay regiones que agregar.

**Paso inductivo:** supongamos que la función es correcta para una lista \(t\). Debemos demostrar que también lo es para una lista formada por un sismo \(s\) seguido de \(t\):

$$
r = s :: t
$$

Existen dos posibilidades:

1. Si \(s.magnitud > u\), la región de \(s\) debe aparecer al comienzo del resultado:

$$
F(s :: t,u) = s.region :: F(t,u)
$$

2. Si \(s.magnitud \leq u\), el sismo no cumple la condición y no debe agregarse:

$$
F(s :: t,u) = F(t,u)
$$

En ambos casos, por hipótesis inductiva, el resultado correspondiente a \(t\) es correcto. Además, el sismo inicial se incorpora únicamente cuando cumple la condición.

Por lo tanto, la función es correcta para cualquier registro finito.

### 2.2. Función `enRegion`

La función `enRegion(r, region)` devuelve las magnitudes de los sismos pertenecientes a una región específica, conservando el orden de aparición.

Su especificación es:

$$
E(r,q) =
[\ s.magnitud \mid s \in r \land s.region = q\ ]
$$

donde \(q\) representa la región solicitada.

**Demostración por inducción estructural:**

**Caso base:** si el registro está vacío:

$$
E(Nil,q) = Nil
$$

No existen magnitudes que devolver, por lo que el resultado es correcto.

**Paso inductivo:** sea \(r = s :: t\). Suponemos que la función devuelve correctamente las magnitudes de la región solicitada para \(t\).

Si el sismo inicial pertenece a la región consultada, se agrega su magnitud al comienzo:

$$
E(s :: t,q) =
s.magnitud :: E(t,q)
$$

siempre que:

$$
s.region = q
$$

En caso contrario:

$$
E(s :: t,q) = E(t,q)
$$

La hipótesis inductiva garantiza que las magnitudes restantes son correctas. Como el primer elemento solamente se agrega cuando pertenece a la región solicitada, no se incorporan magnitudes de otras regiones.

Por lo tanto, la función devuelve exactamente las magnitudes correspondientes, en el orden original.

### 2.3. Función `paresCercanos`

La función `paresCercanos(r, dif)` devuelve las parejas de regiones distintas que tienen al menos dos sismos cuyas magnitudes difieren como máximo en `dif`.

Cada pareja debe aparecer una sola vez. Además, las regiones de las parejas deben respetar el orden de primera aparición en el registro.

Para formalizar la condición, sean \(A\) y \(B\) dos regiones distintas. Se considera que forman una pareja válida si:

$$
\exists s_A,s_B \in r:
$$

$$
s_A.region = A
\land s_B.region = B
\land |s_A.magnitud-s_B.magnitud| \leq dif
$$

Para evitar duplicados, se considera cada pareja siguiendo el orden de primera aparición de sus regiones. Si \(A\) aparece antes que \(B\), se representa como \((A,B)\), nunca como \((B,A)\).

**Argumento de corrección:**

Primero se consideran las regiones distintas que aparecen en el registro, conservando el orden de su primera aparición. Esto garantiza que cada región se considere una sola vez.

Después se examinan las parejas de regiones distintas. Para cada pareja se comprueba si existe al menos un sismo de cada región que satisfaga la diferencia máxima permitida.

La condición matemática utilizada es:

$$
|s_A.magnitud-s_B.magnitud| \leq dif
$$

Si existe al menos una combinación que cumple la condición, la pareja se incluye en el resultado.

La unicidad se garantiza porque cada pareja se construye a partir de dos posiciones distintas de la lista de regiones ordenada, considerando únicamente las combinaciones donde la primera posición es menor que la segunda:

$$
i < j
$$

Así, una pareja no puede generarse en ambos sentidos.

Por consiguiente, el resultado contiene todas las parejas válidas, no contiene parejas de una región consigo misma y no presenta duplicados.

## 3. Punto 2: agrupamientos con mapas

### 3.1. Función `porRegion`

La función `porRegion(r)` agrupa todos los sismos según su región y devuelve un mapa cuya llave es el nombre de la región y cuyo valor es la lista de sismos correspondientes.

Su especificación es:

$$
G(r)(q) =
[\ s \in r \mid s.region = q\ ]
$$

para cada región \(q\) presente en el registro.

**Demostración por inducción estructural:**

**Caso base:** registro vacío.

Si \(r = Nil\), no existen regiones ni sismos que agrupar:

$$
G(Nil) = Map()
$$

El mapa vacío es el resultado correcto.

**Paso inductivo:** supongamos que el agrupamiento de una lista \(t\) es correcto. Consideremos ahora:

$$
r = s :: t
$$

Al agregar el sismo \(s\), existen dos posibilidades:

- Si su región ya tiene una entrada en el mapa, el sismo debe incorporarse a la lista asociada a esa región.
- Si su región todavía no tiene una entrada, debe crearse una nueva asociación entre esa región y una lista que contenga el sismo.

En ambos casos, los sismos previamente agrupados conservan su pertenencia a sus respectivas regiones y el nuevo sismo queda asociado únicamente a su propia región.

Por hipótesis inductiva, todos los sismos de \(t\) ya están correctamente agrupados. Al incorporar \(s\), el resultado sigue siendo correcto.

Por lo tanto, cada sismo queda asociado con su región correspondiente y ninguna región inexistente aparece en el mapa.

### 3.2. Función `conteoPorRegion`

Esta función devuelve la cantidad de sismos registrados en cada región.

Se define a partir del agrupamiento:

$$
C(r)(q) = |G(r)(q)|
$$

donde \(|L|\) representa la cantidad de elementos de una lista \(L\).

Como `porRegion` agrupa correctamente los sismos, contar los elementos de cada grupo produce exactamente la cantidad de registros pertenecientes a cada región.

Para un registro vacío:

$$
C(Nil) = Map()
$$

Además, la cantidad de elementos de una colección no depende del orden en que se recorran sus elementos. Por lo tanto, el conteo es correcto independientemente del orden de recorrido.

### 3.3. Función `magnitudMaxima`

Esta función devuelve la magnitud máxima registrada en cada región.

Su especificación es:

$$
M(r)(q) =
\max \{s.magnitud \mid s \in r \land s.region=q\}
$$

Como cada región presente en el registro contiene al menos un sismo, el máximo está definido para cada grupo.

**Argumento de corrección:**

El agrupamiento garantiza que cada lista contiene únicamente sismos de una misma región. Al obtener las magnitudes de esa lista y calcular su máximo, se obtiene la mayor magnitud registrada en dicha región.

El máximo de una colección finita no depende del orden de sus elementos. Para cualquier permutación \(\pi\) de una lista no vacía \(L\):

$$
\max(L) = \max(\pi(L))
$$

Por lo tanto, la magnitud máxima obtenida para cada región es correcta y no depende del orden de recorrido del registro.

Si el registro está vacío, no existen regiones que resumir y el resultado es:

$$
M(Nil) = Map()
$$

### 3.4. Independencia del orden de recorrido en los agrupamientos

Es importante distinguir entre la pertenencia de los sismos a cada grupo y el orden en que aparecen dentro de las listas.

Sea \(r\) un registro y sea \(\pi(r)\) cualquier permutación de sus elementos.

Para cada región \(q\), el conjunto de sismos que pertenecen a ella es:

$$
S_q(r) = \{s \in r \mid s.region=q\}
$$

Al cambiar el orden de recorrido, no cambia la condición de pertenencia de ningún sismo. Por lo tanto:

$$
S_q(r) = S_q(\pi(r))
$$

Esto permite concluir que:

- Las mismas regiones aparecen como llaves del mapa.
- Los mismos sismos pertenecen a cada región.
- La cantidad de sismos por región no cambia.
- La magnitud máxima de cada región no cambia.

En consecuencia, los resultados de `conteoPorRegion` y `magnitudMaxima` son independientes del orden de recorrido.

**Precisión sobre `porRegion`:** sus valores son listas, y una lista conserva el orden de los elementos que recibe. Por ello, si se invierte el registro, el orden de los sismos dentro de cada lista agrupada puede cambiar. La pertenencia de los sismos a cada región permanece igual, aunque la representación ordenada de las listas no necesariamente sea idéntica.

La independencia del agrupamiento se refiere, por tanto, a qué sismos pertenecen a cada región y no al orden interno de las listas.

## 4. Punto 3: consultas seguras

### 4.1. Función `conteoSeguro`

La función `conteoSeguro(r)` devuelve un mapa que responde cero cuando se consulta una región que no existe.

Su comportamiento puede expresarse como:

$$
CS(r)(q) =
\begin{cases}
C(r)(q), & \text{si } q \text{ existe en el mapa}\\
0, & \text{en otro caso}
\end{cases}
$$

La función parte del resultado de `conteoPorRegion`, que contiene el conteo correcto de todas las regiones presentes.

Al establecer un valor predeterminado de cero, las consultas a regiones desconocidas dejan de producir una excepción y devuelven el valor solicitado por el enunciado.

El valor predeterminado no agrega nuevas regiones al mapa; únicamente define el resultado de una consulta para una llave ausente.

Por lo tanto, la función conserva los conteos existentes y garantiza una respuesta segura para cualquier región consultada.

### 4.2. Función `maximaDe`

La función `maximaDe(r, region)` devuelve la magnitud máxima de una región mediante un valor de tipo `Option[Double]`.

Su especificación es:

$$
D(r,q) =
\begin{cases}
Some(m), & \text{si existe una magnitud máxima } m\\
None, & \text{si no hay sismos de la región } q
\end{cases}
$$

**Argumento de corrección:**

Primero se seleccionan únicamente los sismos cuya región coincide con la solicitada. Después se obtienen sus magnitudes y se calcula el máximo mediante una operación que devuelve un `Option`.

Si la lista de magnitudes no está vacía, existe un máximo y se devuelve mediante `Some`.

Si no hay sismos de esa región, la lista queda vacía y el resultado es `None`.

Por lo tanto, la función devuelve la magnitud máxima cuando existe información y representa correctamente la ausencia de registros sin generar una excepción.

### 4.3. Función `regionMasActiva`

La función `regionMasActiva(r)` devuelve la región con mayor cantidad de sismos.

Si varias regiones tienen el mismo conteo, debe escoger la primera en orden alfabético. Si el registro está vacío, debe devolver `None`.

Sea \(C(r)\) el mapa de conteos por región.

La región buscada debe maximizar la cantidad de sismos:

$$
q^* = \arg\max_q C(r)(q)
$$

En caso de empate, se escoge la región alfabéticamente menor.

**Argumento de corrección:**

El mapa de conteos proporciona la cantidad correcta de sismos para cada región.

Al ordenar las parejas del mapa utilizando primero el conteo en orden descendente y después el nombre de la región en orden ascendente, la primera pareja corresponde necesariamente a la región con mayor actividad.

El primer criterio garantiza que una región con más sismos aparezca antes que una con menos. El segundo garantiza que, cuando dos regiones tengan el mismo conteo, aparezca primero la que tenga el nombre alfabéticamente menor.

Finalmente, al obtener la primera pareja mediante una operación segura, se presentan dos posibilidades:

- Si existen regiones, se devuelve su nombre mediante `Some`.
- Si el registro está vacío, el mapa no contiene elementos y se devuelve `None`.

Por lo tanto, la función cumple las tres condiciones establecidas en el enunciado.

## 5. Punto 4: réplicas perezosas

### 5.1. Función `replicas`

La función `replicas(magnitud)` genera una secuencia infinita de magnitudes, comenzando por la magnitud inicial y multiplicando por \(0.8\) en cada paso.

Los valores se presentan redondeados a dos decimales.

Para formalizar la secuencia, definimos:

$$
x_n = redondear(magnitud \cdot 0.8^n)
$$

para todo entero \(n \geq 0\).

**Demostración por inducción sobre la cantidad de elementos solicitados:**

**Caso base:** primer elemento.

Para \(n=0\):

$$
x_0 = redondear(magnitud \cdot 0.8^0)
$$

Como \(0.8^0=1\):

$$
x_0 = redondear(magnitud)
$$

Este es el primer valor de la secuencia, correspondiente a la magnitud inicial redondeada.

**Paso inductivo:** supongamos que la secuencia produce correctamente los primeros \(n\) valores. El siguiente valor se obtiene aplicando el factor de reducción \(0.8\) una vez más a la magnitud acumulada antes del redondeo de presentación:

$$
x_{n+1} = redondear(magnitud \cdot 0.8^{n+1})
$$

Por lo tanto, cada nuevo elemento corresponde a la magnitud inicial multiplicada por una potencia adicional de \(0.8\), redondeada a dos decimales.

Así, la secuencia generada contiene los valores esperados para cualquier cantidad finita de elementos solicitados.

### 5.2. Evaluación perezosa de la secuencia

La secuencia es infinita porque no se establece una cantidad máxima de réplicas.

Sin embargo, esto no significa que todos sus elementos se calculen inmediatamente.

Una `LazyList` calcula sus elementos a medida que son solicitados. Por lo tanto, cuando se solicita:

```scala
replicas(5.0).take(4).toList
```

la operación `take(4)` limita la evaluación a los primeros cuatro elementos.

La secuencia solicitada es:

$$
x_0 = 5.00
$$

$$
x_1 = 4.00
$$

$$
x_2 = 3.20
$$

$$
x_3 = 2.56
$$

El resultado es:

```scala
List(5.0, 4.0, 3.2, 2.56)
```

No es necesario calcular \(x_4\), \(x_5\) ni los demás elementos para construir esta lista.

Por consiguiente, aunque la secuencia sea infinita, la evaluación de una cantidad finita de elementos requiere calcular únicamente los elementos solicitados.

Esto evita intentar materializar toda la secuencia y permite trabajar con ella sin quedarse esperando a que termine.

### 5.3. Función `replicasSensibles`

La función `replicasSensibles(magnitud, minimo)` devuelve las magnitudes de las réplicas que superan el mínimo establecido.

Su especificación es obtener el prefijo de la secuencia `replicas(magnitud)` cuyos elementos cumplen:

$$
x_n > minimo
$$

**Argumento de corrección:**

La secuencia comienza con la magnitud inicial y sus elementos posteriores representan magnitudes progresivamente menores.

La operación que selecciona las réplicas conserva elementos mientras se cumpla la condición indicada. En el primer elemento que no supera el mínimo, la selección se detiene.

Esto garantiza dos propiedades:

1. Todos los elementos devueltos superan estrictamente el mínimo.
2. Ningún elemento posterior al primero que no cumple la condición se incorpora al resultado.

La primera réplica también se incluye cuando supera el mínimo, pues la condición se evalúa desde el primer elemento de la secuencia.

Si la magnitud inicial no supera el mínimo, el resultado es una lista vacía.

Para magnitudes iniciales positivas y un mínimo positivo, la secuencia disminuye hacia cero. Por ello, eventualmente aparece un elemento que no supera el mínimo y la operación termina.

## 6. Punto 5: informe combinado

### 6.1. Función `informe`

La función `informe(r, minimo)` construye un mapa que asocia cada región con un resumen de sus sismos.

La estructura de cada resumen es:

```scala
case class Resumen(
  sismos: Int,
  maxima: Double,
  replicas: Int
)
```

Para cada región \(q\), el resumen debe contener:

- La cantidad de sismos registrados.
- La magnitud máxima de esos sismos.
- La cantidad de réplicas sensibles que se esperan a partir de la magnitud máxima.

Formalmente, para cada región presente en el registro:

$$
I(r,minimo)(q) =
Resumen(
C(r)(q),
M(r)(q),
|D_r|
)
$$

donde \(D_r\) es la lista de réplicas sensibles generada a partir de la magnitud máxima de la región.

**Argumento de corrección:**

La función utiliza el agrupamiento por región para obtener una lista de sismos correspondiente a cada llave del mapa.

Por la corrección de `porRegion`, cada lista contiene únicamente los sismos de su región.

Para cada grupo se calculan tres valores:

**1. Cantidad de sismos.**

La longitud de la lista representa exactamente la cantidad de registros pertenecientes a la región:

$$
cantidad = |sismos|
$$

**2. Magnitud máxima.**

Se obtienen las magnitudes del grupo y se calcula su máximo:

$$
maxima = \max \{s.magnitud \mid s \in sismos\}
$$

Como cada grupo corresponde a una región presente en el registro, contiene al menos un sismo y el máximo está definido.

**3. Cantidad de réplicas sensibles.**

Se utiliza la magnitud máxima calculada anteriormente como punto de partida para generar las réplicas:

$$
cantidadReplicas =
|replicasSensibles(maxima,minimo)|
$$

Por la corrección de `replicasSensibles`, esta cantidad corresponde exactamente al número de magnitudes generadas que superan el mínimo establecido.

Finalmente, los tres valores se almacenan en un objeto `Resumen` asociado con el nombre de la región.

Como cada región se procesa a partir de su propio grupo, los datos de una región no se mezclan con los de otra.

Por lo tanto, el informe contiene para cada región el conteo, la magnitud máxima y la cantidad correcta de réplicas sensibles.

### 6.2. Caso del registro vacío

Si el registro está vacío, no existen regiones que procesar.

Por la corrección de `porRegion`:

$$
porRegion(Nil) = Map()
$$

Como el informe se construye a partir de las entradas de ese mapa, no se genera ningún resumen.

Por lo tanto:

$$
informe(Nil,minimo) = Map()
$$

Esto coincide con el comportamiento solicitado en el enunciado.

## 7. Independencia de los resultados respecto al orden del registro

Los agrupamientos y los resúmenes deben representar la información del registro, no depender del orden accidental en que se encuentren almacenados sus elementos.

Sea \(r\) un registro y \(\pi(r)\) una permutación de sus sismos.

Para cualquier región \(q\), la pertenencia de un sismo a esa región depende únicamente de su campo `region`, no de su posición en la lista.

Por tanto, al recorrer el registro en otro orden:

$$
S_q(r) = S_q(\pi(r))
$$

De esta igualdad se deduce lo siguiente:

**Conteo por región:**

La cantidad de elementos de cada grupo es la misma:

$$
C(r)(q) = C(\pi(r))(q)
$$

**Magnitud máxima por región:**

El máximo de las mismas magnitudes es independiente de su orden:

$$
M(r)(q) = M(\pi(r))(q)
$$

**Informe por región:**

El conteo, la magnitud máxima y la cantidad de réplicas sensibles se calculan a partir de los mismos sismos y del mismo mínimo. Por consiguiente:

$$
I(r,minimo)(q) =
I(\pi(r),minimo)(q)
$$

para todas las regiones presentes.

En conclusión, los resultados de los conteos, máximos e informes no dependen del orden en que se recorra el registro.

En el caso de `porRegion`, la pertenencia de los sismos a cada grupo tampoco cambia. Sin embargo, como sus valores son listas, el orden interno puede reflejar el orden de recorrido. Esto no modifica el contenido lógico de los grupos ni los resultados de las funciones que calculan cantidades o máximos.

## 8. Conclusión

Las funciones del taller cumplen sus especificaciones mediante operaciones sobre listas, mapas y secuencias perezosas. La inducción estructural permite justificar que las consultas sobre listas consideran correctamente todos los elementos del registro. Los mapas permiten agrupar y resumir la información por región, mientras que las operaciones seguras representan adecuadamente los casos en los que una consulta no tiene resultado.

Por otra parte, la evaluación perezosa permite generar una secuencia infinita de réplicas sin calcular todos sus elementos de antemano. Las operaciones que limitan esa secuencia permiten obtener únicamente los valores necesarios. Finalmente, los conteos, máximos e informes se mantienen iguales ante cualquier cambio en el orden de recorrido del registro, ya que dependen de la pertenencia de los sismos a cada región y no de su posición original.