# Анализ метода Куттера Джордана Боссена на устойчисость к атаке

В качестве атаки использовалось последовательное уменьшать яркость изображения с шагом в 10%.

| Яркость |	Исходный текст | Полученный текст |
| ------- | -------------- | ---------------- |
| Не изм. |	This is a test text for attack analyzing. |	This is a test text for attack analyzing. |
| -10% | This is a test text for attack analyzing. | This is a test text for attack analyzing. |
| -20% | This is a test text for attack analyzing. | This is a test text for attack analyzing. |
| -30% | This is a test text for attack analyzing. | This is a test text for attack analyzing. |
| -40% | This is a test text for attack analyzing. | This is a test text for attack analyzing. |
| -50% | This is a test text for attack analyzing. | This is a test text for attack analyzing. |
| -60% | This is a test text for attack analyzing. | This is a test text for attack analyzing. |
| -90% | This is a test text for attack analyzing. | This is a test text for attack analyzing. |

**Результат: метод Куттера Джордана Боссена устойчив к атаке методом изменения яркости. Закодированный текст не потерялся при уменьшении яркости.**
