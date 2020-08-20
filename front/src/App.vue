<template>
  <v-app id="inspire">
    <v-navigation-drawer
      v-model="drawer"
      app
    >
      <v-list dense>
        <v-list-item link to="/">
          <v-list-item-action>
            <v-icon>mdi-home</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title>Home</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        <v-list-item link to="/ulsearch">
          <v-list-item-action>
            <v-icon>mdi-arrow-up-bold-box</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title>상한가 종목</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
        <v-list-item link to="/up">
          <v-list-item-action>
            <v-icon>mdi-arrow-up-bold-box</v-icon>
          </v-list-item-action>
          <v-list-item-content>
            <v-list-item-title>상한가꼬리잡기 TESTER</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-app-bar
      app
      dark
    >
      <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title>Application</v-toolbar-title>
    </v-app-bar>

    <v-main>
      <v-container
        class="fill-height"
        fluid
      >
        <v-row
          align="center"
          justify="center"
        >
          <v-col class="text-center">
            <router-view />
          </v-col>
        </v-row>
      </v-container>
    </v-main>
    <v-footer
      app
      dark
    >
      <span class="white--text">&copy; {{ new Date().getFullYear() }} SAP</span>
    </v-footer>
  </v-app>
</template>

<script>
// <router-link to="/">Home</router-link> |
  export default {
    props: {
      source: String,
    },
    data: () => ({
      drawer: null,
    }),
  }
</script>