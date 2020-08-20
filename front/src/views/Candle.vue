<template>
  <div>
    <h1>{{info.name}} ({{info.market}}/{{info.symbol}})</h1>
    <h3>{{info.sector}}</h3>
    <h3>{{info.industry}}</h3>
    <v-row>
        <v-col cols="12" sm="12" md="12">
          <trading-vue :data="this.$data" 
            :title-txt="this.info.title" 
            color-title="green"
            color-candle-up="#ee3e07" 
            color-wick-up="#ca0501" 
            color-candle-dw="#009de1" 
            color-wick-dw="#0153c9" 
            color-grid="#3a3d4e26"
            color-vol-up="#ca050140"
            color-vol-dw="#009de140"
            color-back="white">
          </trading-vue>
        </v-col>
    </v-row>
    
  </div>
</template>

<script>
// @ is an alias to /src
// import HelloWorld from '@/components/HelloWorld.vue'
import TradingVue from 'trading-vue-js'

export default {
  name: 'Candle',
  props: ['symbol'],
  components: { TradingVue },
  created() {
    fetch(`/api/series/${this.symbol}`)
      .then(response=>response.json())
      .then(json=>{
        this.info = json.info;
        this.info.title = json.series[0].date.substr(0,10) + '~' + json.series[json.series.length-1].date.substr(0,10);
        json.series.forEach(o=>{
          delete o.symbol;
          delete o.change;
          o.date = new Date(o.date).getTime();
          this.ohlcv.push(Object.values(o));
        })
      })
  },
  data() {
        return {
            info: {},
            ohlcv: []
        }
    }
}
</script>
