package br.com.alura.technews.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.fragments.extensions.mostraErro
import br.com.alura.technews.ui.recyclerview.adapter.ListaNoticiasAdapter
import br.com.alura.technews.ui.viewmodel.ListNoticiasViewModel
import kotlinx.android.synthetic.main.lista_noticias.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.lang.IllegalArgumentException

private const val MENSAGEM_FALHA_CARREGAR_NOTICIAS = "Não foi possível carregar as novas notícias"
private const val TITULO_APPBAR = "Notícias"


class ListaNoticiasFragment : Fragment() {

    private val adapter by lazy {
        context?.let {
            ListaNoticiasAdapter(context = it)
        } ?: throw IllegalArgumentException("Contexto Inválido")
    }

    private val viewModel : ListNoticiasViewModel by viewModel()
    var quandoNoticiaClicado : (noticia : Noticia) -> Unit  = {}
    var quandoFabAddNoticiaClicado : () -> Unit  = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buscaNoticias()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lista_noticias, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = TITULO_APPBAR
        configuraRecyclerView()
        configuraFabAdicionaNoticia()
    }
    private fun configuraFabAdicionaNoticia() {
        lista_noticias_fab_salva_noticia.setOnClickListener {
            quandoFabAddNoticiaClicado()
        }
    }

    private fun configuraRecyclerView() {
        val divisor = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        lista_noticias_recyclerview.addItemDecoration(divisor)
        lista_noticias_recyclerview.adapter = adapter
        configuraAdapter()
    }

    private fun configuraAdapter() {
       adapter.quandoItemClicado = quandoNoticiaClicado
    }

    private fun buscaNoticias() {
        viewModel.buscaTodos().observe(this, Observer { resource ->

            resource.data?.let {
                adapter.atualiza(it)
            }

            resource.error?.let {
                mostraErro(MENSAGEM_FALHA_CARREGAR_NOTICIAS)
            }
        })
    }


}