# XXXXXXXVVIVVIVVIVVILLLLL
A Rechallenge for the broken world.

1.18.2

## 게임 목표
- 무조건 드래곤을 잡고 위더를 잡아야 컨텐츠 종료

## 게임 특징
- 난이도: 어려움
- 3초무적 불가능
- 갈증, 추위 추가

- 갈증은 바이옴 환경에 따라서 정도가 달라짐, 수치는 무제한이며 특정 지점에 도달할때 구속이 걸림. 물약을(물, 신속 물약 등등) 마셔 수치 300을 줄일 수 있고 우유로 수치 150을 줄일 수 있음, 특정 지점에 다다르면 구속이 해제됨 (어떤 상황에서든 물약을 마시면 구속이 해제되니 참고, 허나 갈증 수치가 높을 경우 다시 구속이 걸림)
- 추위는 바이옴 환경에 따라서 정도가 달라짐, 환경에 따라 최대 82/140으로 이루어지며 140에 도달하였을 경우 데미지를 입기 시작 (가루눈 효과랑 동일, 구속 추가), 따뜻한 블럭을 설치하여 추위를 감소 시킬 수 있음. (동시에 구속 제거) 추운 지형에서 나오게 되면 추위가 사라지나 구속은 남아 있으므로 물약, 혹은 우유를 사용해야함.

- 위더스켈레톤 데미지 1.5배 상승
- 사망했을 시 10초간 이동 불가
- 23칸 이상에서 낙하하여 죽지 않았을 시 2/10 확률로 3분동안 부상당한 상태로 변경

- 우유를 마셨을 시 1/3 확률로 기본 우유/독/멀미 효과를 받음
- 자고 일어날 시 1/7 확률로 다중 효과 발생
    - 좋은 컨디션 (신속 II 3분, 힘 II 30초)
    - 두통 (구속 2분)
    - 악몽 (구속 II 3분, 채굴피로 1분)
    - 평범 (신속 1분 30초)
    - 수면마비(가위눌림 / 구속 II 1분, 채굴피로 40초)
    - 낙상 (멀미 1분)
    - 최상 컨디션 (신속 II 3분, 힘 II 15초, 성급함 2분)

### 추운 바이옴 리스트

`SNOWY_PLAINS, ICE_SPIKES, SNOWY_TAIGA, SNOWY_BEACH, GROVE, SNOWY_SLOPES, JAGGED_PEAKS, FROZEN_PEAKS, FROZEN_RIVER, FROZEN_OCEAN, DEEP_OCEAN, DEEP_FROZEN_OCEAN, COLD_OCEAN, DEEP_COLD_OCEAN, DRIPSTONE_CAVES, SOUL_SAND_VALLEY, WARPED_FOREST`

참고:
- 지옥 바이옴 중 `SOUL_SAND_VALLEY, WARPED_FOREST`가 추운 바이옴으로 등록되어있음.
- SNOWY, FROZEN이 포함된 바이옴과 ICE_SPIKES, GROVE는 140까지 추위가 상승.

### 따뜻한 바이옴 리스트 (갈증이 2씩 증가)

`DESERT,SAVANNA, SAVANNA_PLATEAU, WINDSWEPT_SAVANNA,BADLANDS, WOODED_BADLANDS, ERODED_BADLANDS, WARM_OCEAN, LUKEWARM_OCEAN, DEEP_LUKEWARM_OCEAN`

### 뜨거운 바이옴 (지옥 바이옴 / 갈증이 3씩 증가)

`NETHER_WASTES, BASALT_DELTAS, CRIMSON_FOREST`

#### 여담:

- 방송쪽으로 진행 할 수 있도록 옵션을 설정하였음. 플러그인 로딩 후 plugins -> XXXXXXXVVIVVIVVIVVILLLLL -> config.yml 에서 system-message 값을 false로 바꾸면 오프닝 / 엔딩 메시지가 나오지 않음.

---

**_What do you see?_**

즐거운 게임 되시길 바랍니다.
