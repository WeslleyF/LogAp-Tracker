import {
  Badge,
  Group,
  Loader,
  Progress,
  Select,
  SimpleGrid,
  Stack,
  Table,
  Text,
  Title,
} from '@mantine/core';
import { DateInput } from '@mantine/dates';
import { IconTrophy } from '@tabler/icons-react';
import dayjs from 'dayjs';
import { useState } from 'react';
import {
  Bar,
  BarChart,
  CartesianGrid,
  LabelList,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { ChartCard } from '../../components/dashboard/ChartCard';
import { StatCard } from '../../components/dashboard/StatCard';
import { StatusManutencaoBadge, ROTULOS_STATUS } from '../../components/StatusManutencaoBadge';
import { useVeiculos } from '../../components/crud/useOpcoes';
import { formatarData, formatarKm, formatarMoeda, formatarNumero } from '../../lib/formatadores';
import { useDashboard } from './useDashboard';

const COR_BARRA = '#1c7ed6';

export function DashboardPage() {
  const [veiculoId, setVeiculoId] = useState<number | null>(null);
  const [dataInicial, setDataInicial] = useState(() => dayjs().format('YYYY-MM-DD'));
  const { data, isLoading, isError } = useDashboard(veiculoId, dataInicial);
  const { data: veiculos = [] } = useVeiculos();

  if (isLoading) return <Loader />;
  if (isError || !data) return <Text c="red">Não foi possível carregar o dashboard.</Text>;

  const { totalKm, volumeCategoria, rankingUtilizacao, cronograma, projecaoFinanceira } = data;
  const destaque = rankingUtilizacao[0];
  const maiorKm = destaque?.totalKm || 1;
  const volume = volumeCategoria.map((v) => ({
    categoria: v.tipo === 'LEVE' ? 'Leve' : 'Pesado',
    viagens: v.totalViagens,
  }));

  return (
    <Stack>
      <Title order={2}>Dashboard</Title>

      <SimpleGrid cols={{ base: 1, md: 2, xl: 3 }}>
        <StatCard titulo="KM total (viagens realizadas)" valor={formatarKm(totalKm.totalKm)}>
          <Select
            size="xs"
            clearable
            searchable
            placeholder="Toda a frota"
            data={veiculos.map((v) => ({ value: String(v.id), label: `${v.placa} · ${v.modelo}` }))}
            value={veiculoId == null ? null : String(veiculoId)}
            onChange={(v) => setVeiculoId(v ? Number(v) : null)}
          />
        </StatCard>

        <ChartCard titulo="Viagens realizadas por categoria">
          <ResponsiveContainer width="100%" height={160}>
            <BarChart data={volume} margin={{ top: 16, right: 8, left: 0, bottom: 0 }}>
              <CartesianGrid vertical={false} strokeOpacity={0.3} />
              <XAxis dataKey="categoria" tickLine={false} />
              <YAxis allowDecimals={false} width={30} tickLine={false} axisLine={false} />
              <Tooltip formatter={(valor) => [formatarNumero(Number(valor)), 'Viagens']} />
              <Bar dataKey="viagens" fill={COR_BARRA} radius={[4, 4, 0, 0]} maxBarSize={64}>
                <LabelList dataKey="viagens" position="top" />
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </ChartCard>

        <ChartCard titulo="Ranking de utilização (km)">
          <Stack gap="xs">
            {rankingUtilizacao.map((v) => (
              <div key={v.veiculoId}>
                <Group justify="space-between" wrap="nowrap">
                  <Group gap={6} wrap="nowrap">
                    {v.posicao === 1 && (
                      <IconTrophy size={16} color="#f08c00" aria-label="Destaque" />
                    )}
                    <Text size="sm" fw={v.posicao === 1 ? 700 : 400}>
                      {v.posicao}. {v.placa} · {v.modelo}
                    </Text>
                    {v.posicao === 1 && (
                      <Badge color="yellow" variant="light" size="xs">
                        Destaque
                      </Badge>
                    )}
                  </Group>
                  <Text size="sm">{formatarKm(v.totalKm)}</Text>
                </Group>
                <Progress value={(v.totalKm / maiorKm) * 100} size="xs" mt={2} />
              </div>
            ))}
          </Stack>
        </ChartCard>

        <ChartCard
          titulo="Próximas manutenções"
          acao={
            <DateInput
              size="xs"
              mb="xs"
              label="A partir de"
              valueFormat="DD/MM/YYYY"
              value={dayjs(dataInicial).toDate()}
              onChange={(d) => d && setDataInicial(dayjs(d).format('YYYY-MM-DD'))}
            />
          }
        >
          {cronograma.length === 0 ? (
            <Text size="sm" c="dimmed">
              Nenhuma manutenção pendente a partir desta data.
            </Text>
          ) : (
            <Table.ScrollContainer minWidth={320}>
              <Table verticalSpacing={4} fz="sm">
                <Table.Tbody>
                  {cronograma.map((m) => (
                    <Table.Tr key={m.id}>
                      <Table.Td>{formatarData(m.dataInicio)}</Table.Td>
                      <Table.Td>
                        {m.placa}
                        <Text span size="xs" c="dimmed">
                          {' '}
                          {m.modelo}
                        </Text>
                      </Table.Td>
                      <Table.Td>{m.tipoServico}</Table.Td>
                      <Table.Td>
                        <StatusManutencaoBadge status={m.status} />
                      </Table.Td>
                    </Table.Tr>
                  ))}
                </Table.Tbody>
              </Table>
            </Table.ScrollContainer>
          )}
        </ChartCard>

        <StatCard
          titulo={`Projeção financeira de ${dayjs(projecaoFinanceira.mesReferencia).format('MMMM/YYYY')}`}
          valor={formatarMoeda(projecaoFinanceira.total)}
        >
          <Text size="xs" c="dimmed" mb={4}>
            {projecaoFinanceira.quantidade} manutenções, sem as rejeitadas
          </Text>
          <Table verticalSpacing={2} fz="sm">
            <Table.Tbody>
              {projecaoFinanceira.porStatus.map((s) => (
                <Table.Tr key={s.status}>
                  <Table.Td>{ROTULOS_STATUS[s.status]}</Table.Td>
                  <Table.Td ta="right">{s.quantidade}</Table.Td>
                  <Table.Td ta="right">{formatarMoeda(s.total)}</Table.Td>
                </Table.Tr>
              ))}
            </Table.Tbody>
          </Table>
        </StatCard>
      </SimpleGrid>
    </Stack>
  );
}
