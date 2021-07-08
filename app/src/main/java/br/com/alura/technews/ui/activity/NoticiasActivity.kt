package br.com.alura.technews.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.activity.extensions.transacaoFragment
import br.com.alura.technews.ui.fragments.ListaNoticiasFragment
import br.com.alura.technews.ui.fragments.VisualizaNoticiaFragment
import kotlinx.android.synthetic.main.activity_noticias.*

private const val TAG_FRAGMENT_VISUALIZA_NOTICIA = "visualiza_noticia"
class NoticiasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticias)
        configuraFragmentPeloEstado(savedInstanceState)

    }

    private fun configuraFragmentPeloEstado(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            abreListaNoticias()
        } else {
            tentaReabrirFragment()
        }
    }

    private fun tentaReabrirFragment() {
        supportFragmentManager
            .findFragmentByTag(TAG_FRAGMENT_VISUALIZA_NOTICIA)?.let { fragment ->
                val argumentos = fragment.arguments
                val novoFragment = VisualizaNoticiaFragment()
                novoFragment.arguments = argumentos
                removeFragmentVisualizaNoticia(novoFragment)
                transacaoFragment {
                    val container = configuraContainer()
                    replace(container, novoFragment, TAG_FRAGMENT_VISUALIZA_NOTICIA)
                }
            }
    }

    private fun FragmentTransaction.configuraContainer() =
        if (activity_noticias_container_primario != null) {
            R.id.activity_noticias_container_secundario
        } else {
            addToBackStack(null)
            R.id.activity_noticias_container_primario
        }

    private fun abreListaNoticias() {
        transacaoFragment {
            replace(R.id.activity_noticias_container_primario, ListaNoticiasFragment())
        }
    }

    private fun abreFormularioModoCriacao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        startActivity(intent)
    }

    private fun abreVisualizadorNoticia(noticia: Noticia) {
        val fragment = VisualizaNoticiaFragment()
        val dados = Bundle()
        dados.putLong(NOTICIA_ID_CHAVE, noticia.id)
        fragment.arguments = dados

        transacaoFragment {
         val container = configuraContainer()
          replace(container, fragment, TAG_FRAGMENT_VISUALIZA_NOTICIA)
        }
    }



    private fun abreFormularioEdicao(noticia: Noticia) {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, noticia.id)
        startActivity(intent)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)


        when (fragment) {
            is ListaNoticiasFragment -> {
                configuraListaNoticias(fragment)
            }

            is VisualizaNoticiaFragment -> {
                configuraVisualizaNoticias(fragment)
            }
        }
    }



    private fun configuraVisualizaNoticias(fragment: VisualizaNoticiaFragment) {
        fragment.quandoFinaliza = {
            supportFragmentManager
                .findFragmentByTag(TAG_FRAGMENT_VISUALIZA_NOTICIA)?.let { fragment ->
                    removeFragmentVisualizaNoticia(fragment)
                }
        }
        fragment.quandoSelecionaMenuEdicao = this::abreFormularioEdicao
    }

    private fun removeFragmentVisualizaNoticia(fragment: Fragment) {
        transacaoFragment {
            remove(fragment)
        }
        supportFragmentManager.popBackStack()
    }

    private fun configuraListaNoticias(fragment: ListaNoticiasFragment) {
        fragment.quandoNoticiaClicado = this::abreVisualizadorNoticia
        fragment.quandoFabAddNoticiaClicado = this::abreFormularioModoCriacao
    }

}
