<template>
  <div>
    <v-row>
    <v-col cols="12" sm="2" md="2">
      <v-menu
        ref="menu"
        v-model="menu"
        :close-on-content-click="false"
        :return-value.sync="dt"
        transition="scale-transition"
        offset-y
        min-width="290px"
        change="ulsearch"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-text-field
            v-model="dt"
            label="일자"
            prepend-icon="mdi-calendar"
            readonly
            v-bind="attrs"
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker v-model="dt" no-title scrollable>
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="$refs.menu.save(dt)">확인</v-btn>
        </v-date-picker>
      </v-menu>
    </v-col>
    <v-col cols="12" sm="2" md="2">
      <v-btn @click="search()">조회</v-btn>
    </v-col>
    </v-row>
    <v-row>
      <v-col cols="12" sm="12" md="12">
      <v-simple-table dense>
        <template v-slot:default>
          <thead>
            <tr>
              <th class="text-center">Symbol</th>
              <th class="text-center">name</th>
              <th class="text-center">change</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in uls" :key="item.symbol" @click="candle(item.symbol)">
              <td>{{ item.symbol }}</td>
              <td>{{ item.name }}</td>
              <td>{{ item.change }}</td>
            </tr>
          </tbody>
        </template>
      </v-simple-table>
      </v-col>
    </v-row>
  </div>
</template>

<script>
export default {
  data: () => ({
      dt: new Date().toISOString().substr(0, 10),
      menu: false,
      uls: []
    }),
  methods: {
    search() {
       fetch(`/api/ul/${this.dt}`)
        .then(response=>response.json())
        .then(json=>{
          this.uls = json;
        })
    },
    candle(symbol) {
      this.$router.push(`/candle/${symbol}`);
    }
  },
}

</script>