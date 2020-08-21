<template>
  <div>
    <v-row>
    <v-col cols="12" sm="2" md="2">
      <v-menu
        ref="menu"
        v-model="sdtmenu"
        :close-on-content-click="false"
        transition="scale-transition"
        offset-y
        min-width="290px"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-text-field
            v-model="sdt"
            label="시작일자"
            prepend-icon="mdi-calendar"
            readonly
            v-bind="attrs"
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker v-model="sdt" no-title scrollable>
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="sdtmenu = false">확인</v-btn>
        </v-date-picker>
      </v-menu>
    </v-col>
    <v-col cols="12" sm="2" md="2">
      <v-menu
        ref="menu"
        v-model="edtmenu"
        :close-on-content-click="false"
        transition="scale-transition"
        offset-y
        min-width="290px"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-text-field
            v-model="edt"
            label="종료일자"
            prepend-icon="mdi-calendar"
            readonly
            v-bind="attrs"
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker v-model="edt" no-title scrollable>
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="edtmenu = false">확인</v-btn>
        </v-date-picker>
      </v-menu>
    </v-col>
    <v-col cols="12" sm="2" md="2">
      <v-select :items="lp" label="손절매(%)" v-model="l"></v-select>
    </v-col>
    <v-col cols="12" sm="2" md="2">
      <v-select :items="up" label="차익실현(%)" v-model="u"></v-select>
    </v-col>
    <v-col cols="12" sm="2" md="2">
      <v-select :items="buyp" label="매수비중(%)" v-model="p"></v-select>
    </v-col>
    <v-col cols="12" sm="2" md="2">
      <v-btn @click="test()">TEST</v-btn>
    </v-col>
    </v-row>
    <v-row>
      <v-col>
        시작금액: [{{sp}} 원] , 종료금액: [{{ep}} 원], 수익률 [{{epp}} %]
      </v-col>
    </v-row>
    <v-row>
      <v-expansion-panels>
        <v-expansion-panel
          v-for="(item,i) in logs"
          :key="i"
        >
          <v-expansion-panel-header>{{item.title}}</v-expansion-panel-header>
          <v-expansion-panel-content style="white-space: pre-wrap;">
              {{item.contents}}
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
    </v-row>
  </div>
</template>

<script>
export default {
  data: () => ({
      sdt: new Date().toISOString().substr(0, 10),
      edt: new Date().toISOString().substr(0, 10),
      sdtmenu: false,
      edtmenu: false,
      lp: [...Array(31).keys()].map(i=>i-30).reverse(),
      up: [...Array(31).keys()],
      buyp: [...Array(10).keys()].map(i=>(i+1)*10).reverse(),
      l: 0,
      u: 9,
      p: 100,
      sp: 10000000,
      ep: 0,
      epp: 0,
      logs: []
    }),
    methods: {
      test() {
        fetch(`/api/test/up?sDt=${this.sdt}&eDt=${this.edt}&lp=${this.l}&up=${this.u}&bp=${this.p}`)
        .then(response=>response.json())
        .then(json=>{
          //console.log(json);
          this.logs=[];
          this.sp = json.sbal;
          this.ep = json.ebal;
          this.epp = json.er;
          for(let log of json.logs){
            let c = '';
            for(let l of log.logs){
              c += l +"\n";
            }
            this.logs.push({
              title: log.dt + '  ( *잔고' + log.balance +' 원 )',
              contents: c
            })
          }
        })
      }
    }
}

</script>